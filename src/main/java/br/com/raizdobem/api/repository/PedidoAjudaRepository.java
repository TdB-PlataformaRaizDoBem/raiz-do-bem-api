package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.PedidoAjuda;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PedidoAjudaRepository implements PanacheRepository<PedidoAjuda> {

    public List<PedidoAjuda> listarTodos(){
        return listAll();
    }

    public void criar(PedidoAjuda pedidoAjudaDTO){
        persist(pedidoAjudaDTO);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
