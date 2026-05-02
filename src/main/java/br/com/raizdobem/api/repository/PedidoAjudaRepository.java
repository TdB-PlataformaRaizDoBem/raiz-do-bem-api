package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.PedidoAjuda;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PedidoAjudaRepository implements PanacheRepository<PedidoAjuda> {

    public List<PedidoAjuda> listarTodos(){
        return listAll();
    }

    @Transactional
    public void criar(PedidoAjuda pedidoAjuda){
        persist(pedidoAjuda);
    }

    @Transactional
    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
