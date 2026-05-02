package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.dto.DentistaDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DentistaRepository implements PanacheRepository<DentistaDTO> {

    public void criar(DentistaDTO dentistaDTO){
        persist(dentistaDTO);
    }

    public List<DentistaDTO> listarTodos(){
        return listAll();
    }

    public List<DentistaDTO> listarPorCidade(String cidade){
        return list("cidade = ?1", cidade);
    }

    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
