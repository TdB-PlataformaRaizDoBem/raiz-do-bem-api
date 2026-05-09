package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarPedidoAjudaDTO;
import br.com.raizdobem.api.dto.request.CriarPedidoAjudaDTO;
import br.com.raizdobem.api.entity.*;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RegraNegocioException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@ApplicationScoped
public class PedidoAjudaService {
    @Inject
    PedidoAjudaRepository repository;

    @Inject
    EnderecoService enderecoService;

    @Inject
    DentistaService dentistaService;

    @Transactional
    public PedidoAjuda criar(CriarPedidoAjudaDTO dto) {
        PedidoAjuda pedidoAjuda = new PedidoAjuda();

        pedidoAjuda.setCpf(dto.getCpf());
        pedidoAjuda.setNomeCompleto(dto.getNome());
        pedidoAjuda.setDataNascimento(dto.getDataNascimento());
        pedidoAjuda.setStatus(StatusPedido.PENDENTE);

        if (dto.getSexo().equals("M")) {
            if(invalidarHomens(dto.getDataNascimento()))
                pedidoAjuda.setStatus(StatusPedido.REJEITADO);
        }

        pedidoAjuda.setSexo(Sexo.valueOf(dto.getSexo().toUpperCase()));
        pedidoAjuda.setTelefone(dto.getTelefone());
        pedidoAjuda.setEmail(dto.getEmail());
        pedidoAjuda.setDescricaoProblema(dto.getDescricaoProblema());
        pedidoAjuda.setDataPedido(LocalDate.now());

        Endereco endereco = enderecoService.criar(dto.getEndereco());
        if(endereco == null){
            throw new NaoEncontradoException("Endereço não encontrado.");
        }
        pedidoAjuda.setEndereco(endereco);

        repository.criar(pedidoAjuda);
        return pedidoAjuda;
    }

    public List<PedidoAjuda> listarTodos() {
        return repository.listarTodos();
    }

    public PedidoAjuda buscarPorCpf(String cpf) {
        return repository.buscarPorCpf(cpf);
    }

    public List<PedidoAjuda> listarPorData(LocalDate dataPedido) {
        return repository.listarPorData(dataPedido);
    }

    @Transactional
    public PedidoAjuda processarPedido(long id, AtualizarPedidoAjudaDTO dto){
        PedidoAjuda pedido = repository.findById(id);
        if(pedido == null)
            throw new NaoEncontradoException("Pedido de ajuda não encontrado.");

        if(pedido.getStatus() == StatusPedido.REJEITADO)
            throw new RegraNegocioException("Pedido REJEITADO não pode ser processado.");

        if(dto.getStatusPedido().equals(StatusPedido.PENDENTE))
            throw new ValidacaoException("Pedido só pode ser atualizado para APROVADO/REJEITADO.");

        StatusPedido novoStatus;
        try{
            novoStatus = dto.getStatusPedido();
        } catch(Exception e){
            throw new ValidacaoException("Status INVÁLIDO. Novo status pode ser APROVADO ou REJEITADO");
        }

        if(novoStatus == StatusPedido.APROVADO && dto.getIdDentista() > 0){
            Dentista dentista = dentistaService.buscarPorId(id);
            if(dentista == null)
                throw new NaoEncontradoException("Dentista aprovador não encontrado.");

            if(dentista.getCategoria().equals("COORDENADOR"))
                pedido.setDentista(dentista);
            else{
                throw new RegraNegocioException("Apenas um Dentista Coordenador pode aprovar um pedido de ajuda.");
            }
        }
        pedido.setStatus(novoStatus);
        return pedido;
    }
    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }

    public static boolean invalidarHomens(LocalDate dataNasc){
        if (dataNasc == null) {
            return false;
        }
        Period idade = Period.between(dataNasc, LocalDate.now());
        return idade.getYears() >= 18;
    }
}
