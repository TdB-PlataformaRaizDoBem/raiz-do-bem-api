package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.dto.PedidoAjudaDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PedidoAjudaRepository implements PanacheRepository<PedidoAjudaDTO> {

    public List<PedidoAjudaDTO> listarTodos(){
        return listAll();
    }

    public void criar(PedidoAjudaDTO pedidoAjudaDTO){
        persist(pedidoAjudaDTO);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
