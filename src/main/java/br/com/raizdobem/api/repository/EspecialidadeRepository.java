package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.dto.EspecialidadeDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EspecialidadeRepository implements PanacheRepository<EspecialidadeDTO> {
    public List<EspecialidadeDTO> listarTodas(){
        return listAll();
    }
}
