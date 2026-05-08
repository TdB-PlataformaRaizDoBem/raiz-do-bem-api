package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.AtualizarPedidoAjudaDTO;
import br.com.raizdobem.api.dto.CriarPedidoAjudaDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.model.PedidoAjuda;
import br.com.raizdobem.api.model.Sexo;
import br.com.raizdobem.api.model.StatusPedido;
import br.com.raizdobem.api.repository.EnderecoRepository;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.core.Request;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@ApplicationScoped
public class PedidoAjudaService {
    @Inject
    PedidoAjudaRepository repository;

    @Inject
    EnderecoRepository enderecoRepository;
    @Inject
    PedidoAjudaRepository pedidoAjudaRepository;

    @Transactional
    public PedidoAjuda criar(CriarPedidoAjudaDTO dto) {
        PedidoAjuda pedidoAjuda = new PedidoAjuda();

        pedidoAjuda.setCpf(dto.getCpf());
        pedidoAjuda.setNomeCompleto(dto.getNome());
        pedidoAjuda.setDataNascimento(dto.getDataNascimento());
        pedidoAjuda.setStatus(StatusPedido.PENDENTE);

        if(dto.getSexo().equals("M")){
            if(invalidarHomens(dto.getDataNascimento())){
                pedidoAjuda.setStatus(StatusPedido.REJEITADO);
            }
        }

        pedidoAjuda.setSexo(Sexo.valueOf(dto.getSexo().toUpperCase()));
        pedidoAjuda.setTelefone(dto.getTelefone());
        pedidoAjuda.setEmail(dto.getEmail());
        pedidoAjuda.setDescricaoProblema(dto.getDescricaoProblema());
        pedidoAjuda.setDataPedido(LocalDate.now());

        Endereco endereco = enderecoRepository.buscarPeloId(dto.getIdEndereco());
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

    @Transactional
    public PedidoAjuda processarPedido(long id, AtualizarPedidoAjudaDTO dto){
        PedidoAjuda pedido = pedidoAjudaRepository.findById(id);
        return pedido;
    }
    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }

    public static boolean invalidarHomens(LocalDate dataNasc){
        Period idade = Period.between(dataNasc, LocalDate.now());
        return idade.getYears() >= 18;
    }
}
