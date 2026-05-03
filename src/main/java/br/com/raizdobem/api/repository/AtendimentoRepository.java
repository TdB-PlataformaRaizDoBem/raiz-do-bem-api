package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Atendimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AtendimentoRepository implements PanacheRepository<Atendimento> {

    public void criar(Atendimento atendimento){
        persist(atendimento);
    }

    public List<Atendimento> listarTodos(){
        return listAll();
    }

//    public Atendimento buscarPeloCpf(String cpf){
//        return find("cpf", cpf);
//    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
