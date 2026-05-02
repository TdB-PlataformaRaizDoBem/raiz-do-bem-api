package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Atendimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AtendimentoRepository implements PanacheRepository<Atendimento> {

    public List<Atendimento> listarTodos(){
        return listAll();
    }

    @Transactional
    public void criar(Atendimento atendimento){
        persist(atendimento);
    }

    @Transactional
    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
