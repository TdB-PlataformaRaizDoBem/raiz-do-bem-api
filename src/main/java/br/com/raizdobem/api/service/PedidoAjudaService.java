package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.PedidoAjudaUpdateRequest;
import br.com.raizdobem.api.dto.request.PedidoAjudaCreateRequest;
import br.com.raizdobem.api.dto.response.PedidoAjudaResponse;
import br.com.raizdobem.api.entity.*;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RegraNegocioException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.mapper.PedidoAjudaMapper;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import br.com.raizdobem.api.util.CpfValidatorUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static br.com.raizdobem.api.mapper.PedidoAjudaMapper.mapeamentoListaPedidos;
import static br.com.raizdobem.api.mapper.PedidoAjudaMapper.mapeamentoPedido;

@Transactional
@ApplicationScoped
public class PedidoAjudaService {
    @Inject
    PedidoAjudaRepository repository;

    @Inject
    EnderecoService enderecoService;

    @Inject
    DentistaService dentistaService;

    public PedidoAjudaResponse criar(PedidoAjudaCreateRequest dto) {
        if(!CpfValidatorUtil.cpfValido(dto.cpf())){
            throw new ValidacaoException("Cpf inválido");
        }

        PedidoAjuda pedidoAjuda = new PedidoAjuda();

        pedidoAjuda.setCpf(dto.cpf());
        pedidoAjuda.setNomeCompleto(dto.nome());
        pedidoAjuda.setDataNascimento(dto.dataNascimento());
        pedidoAjuda.setStatus(StatusPedido.PENDENTE);

        if(dto.sexo() == null)
            throw new ValidacaoException("Sexo é obrigatório.");
        if (("M").equals(dto.sexo())) {
            if(maiorIdade(dto.dataNascimento()))
                pedidoAjuda.setStatus(StatusPedido.REJEITADO);
        }

        pedidoAjuda.setSexo(Sexo.valueOf(dto.sexo().toUpperCase()));
        pedidoAjuda.setTelefone(dto.telefone());
        pedidoAjuda.setEmail(dto.email());
        pedidoAjuda.setDescricaoProblema(dto.descricaoProblema());
        pedidoAjuda.setDataPedido(LocalDate.now());

        Endereco endereco = enderecoService.criarComoSuporte(dto.endereco(), TipoEndereco.RESIDENCIAL);
        if(endereco == null){
            throw new NaoEncontradoException("Endereço não encontrado.");
        }
        pedidoAjuda.setEndereco(endereco);

        repository.criar(pedidoAjuda);

        return PedidoAjudaMapper.mapeamentoPedido(pedidoAjuda);
    }

    public List<PedidoAjudaResponse> listarTodos() {
        List<PedidoAjuda> pedidos = repository.listarTodos();
        return mapeamentoListaPedidos(pedidos);
    }

    public PedidoAjudaResponse buscarPorCpf(String cpf) {
        PedidoAjuda pedido = repository.buscarPorCpf(cpf);
        if (pedido == null) {
            throw new NaoEncontradoException("Pedido de ajuda não encontrado.");
        }
        return mapeamentoPedido(pedido);
    }

    public PedidoAjuda buscarEntidadePorId(Long id) {
        return repository.findById(id);
    }

    public List<PedidoAjudaResponse> listarPorData(LocalDate dataPedido) {
        List<PedidoAjuda> pedidos = repository.listarPorData(dataPedido);
        return mapeamentoListaPedidos(pedidos);
    }

    public PedidoAjudaResponse processarPedido(long id, PedidoAjudaUpdateRequest dto){
        PedidoAjuda pedido = repository.findById(id);
        if(pedido == null)
            throw new NaoEncontradoException("Pedido de ajuda não encontrado.");

        if(pedido.getStatus() == StatusPedido.REJEITADO)
            throw new RegraNegocioException("Pedido REJEITADO não pode ser processado.");

        if(dto.statusPedido() == null || dto.statusPedido().equals(StatusPedido.PENDENTE))
            throw new ValidacaoException("Pedido só pode ser atualizado para APROVADO/REJEITADO.");

        StatusPedido novoStatus;
        try{
            novoStatus = dto.statusPedido();
        } catch(Exception e){
            throw new ValidacaoException("Status INVÁLIDO. Novo status pode ser APROVADO ou REJEITADO");
        }

        if(novoStatus == StatusPedido.APROVADO && dto.idDentista() > 0){
            Dentista dentista = dentistaService.buscarEntidadePorId(dto.idDentista());

            if(dentista == null)
                throw new NaoEncontradoException("Dentista aprovador não encontrado.");

            if(dentista.getCategoria().equals("COORDENADOR"))
                pedido.setDentista(dentista);
            else{
                throw new RegraNegocioException("Apenas um Dentista Coordenador pode aprovar um pedido de ajuda.");
            }
        }
        pedido.setStatus(novoStatus);

        return mapeamentoPedido(pedido);
    }

    public boolean excluir(Long id) {
        return repository.excluir(id);
    }

    public static boolean maiorIdade(LocalDate dataNasc){
        if (dataNasc == null) {
            return false;
        }
        Period idade = Period.between(dataNasc, LocalDate.now());
        return idade.getYears() >= 18;
    }
}
