package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.entity.ProgramaSocial;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProgramaRepository implements PanacheRepository<ProgramaSocial> {
    public List<ProgramaSocial> listarTodos(){
        return listAll();
    }

    public ProgramaSocial buscarPorId(long id){
        return findById(id);
    }
}
