package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.AtualizarPedidoAjudaDTO;
import br.com.raizdobem.api.entity.PedidoAjuda;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PedidoAjudaRepository implements PanacheRepository<PedidoAjuda> {

    public List<PedidoAjuda> listarTodos(){
        return listAll();
    }

    public void criar(PedidoAjuda pedidoAjudaDTO){
        persist(pedidoAjudaDTO);
    }

    public PedidoAjuda buscarPorCpf(String cpf){
        return find("cpf", cpf).firstResult();
    }

    public List<PedidoAjuda> listarPorData(LocalDate dataPedido) {
        return list("dataPedido", dataPedido);
    }

    public void atualizar(String cpf, AtualizarPedidoAjudaDTO request){
        // Atualização efetiva é feita no service (transacional) para aplicar regra de negócio.
        find("cpf", cpf).firstResult();
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
