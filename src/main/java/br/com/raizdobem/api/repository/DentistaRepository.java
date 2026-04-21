package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Dentista;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DentistaRepository implements PanacheRepository<Dentista> {
    public List<Dentista> listarTodos(){
        return listAll();
    }
}
