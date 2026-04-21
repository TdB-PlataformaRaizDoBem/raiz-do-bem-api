package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Especialidade;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EspecialidadeRepository implements PanacheRepository<Especialidade> {
    public List<Especialidade> listarTodas(){
        return listAll();
    }
}
