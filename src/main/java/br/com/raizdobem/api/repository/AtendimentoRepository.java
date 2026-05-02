package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.dto.AtendimentoDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AtendimentoRepository implements PanacheRepository<AtendimentoDTO> {

    public void criar(AtendimentoDTO atendimentoDTO){
        persist(atendimentoDTO);
    }

    public List<AtendimentoDTO> listarTodos(){
        return listAll();
    }

//    public AtendimentoDTO buscarPeloCpf(String cpf){
//        return find("cpf", cpf);
//    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
