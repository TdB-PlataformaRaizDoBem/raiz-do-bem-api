package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Colaborador;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ColaboradorRepository implements PanacheRepository<Colaborador> {

    public List<Colaborador> listarTodos(){
        return listAll();
    }

    public void criar(Colaborador colaborador){
        persist(colaborador);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
