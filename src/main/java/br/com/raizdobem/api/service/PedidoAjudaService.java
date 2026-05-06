package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.CriarPedidoAjudaDTO;
import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.model.PedidoAjuda;
import br.com.raizdobem.api.model.Sexo;
import br.com.raizdobem.api.model.StatusPedido;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.core.Request;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@ApplicationScoped
public class PedidoAjudaService {
    @Inject
    PedidoAjudaRepository repository;

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

        Sexo sexoSolicitante = Sexo.valueOf(dto.getSexo());
        pedidoAjuda.setSexo(sexoSolicitante);


        pedidoAjuda.setTelefone(dto.getTelefone());
        pedidoAjuda.setEmail(dto.getEmail());
        pedidoAjuda.setDescricaoProblema(dto.getEmail());
        pedidoAjuda.setDataPedido(LocalDate.now());
        int enderecoSolicitante = dto.getIdEndereco();

//        Corrigir como linkar o endereco ao pedido de ajuda, pois o idEndereco é um inteiro e o pedidoAjuda tem um objeto do tipo Endereco, não um inteiro
//        pedidoAjuda.setEndereco(dto.getIdEndereco());
        repository.criar(pedidoAjuda);
        return pedidoAjuda;
    }

    public List<PedidoAjuda> listarTodos() {
        return repository.listarTodos();
    }

    public void atualizarPedido(){

    }
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }

    public static boolean invalidarHomens(LocalDate dataNasc){
        Period idade = Period.between(dataNasc, LocalDate.now());
        return idade.getYears() >= 18;
    }
}
