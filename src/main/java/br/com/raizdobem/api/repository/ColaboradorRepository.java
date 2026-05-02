package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.dto.ColaboradorDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ColaboradorRepository implements PanacheRepository<ColaboradorDTO> {

    public List<ColaboradorDTO> listarTodos(){
        return listAll();
    }

    public void criar(ColaboradorDTO colaboradorDTO){
        persist(colaboradorDTO);
    }

    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
