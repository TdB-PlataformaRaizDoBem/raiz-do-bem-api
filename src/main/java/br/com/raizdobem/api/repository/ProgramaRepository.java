package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.dto.ProgramaSocialDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProgramaRepository implements PanacheRepository<ProgramaSocialDTO> {
    public List<ProgramaSocialDTO> listarTodos(){
        return listAll();
    }
}
