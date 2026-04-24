package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Atendimento;
import br.com.raizdobem.api.model.Colaborador;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AtendimentoRepository implements PanacheRepository<Atendimento> {

    public List<Atendimento> listarTodos(){
        return listAll();
    }

    public void criar(Atendimento atendimento){
        persist(atendimento);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
