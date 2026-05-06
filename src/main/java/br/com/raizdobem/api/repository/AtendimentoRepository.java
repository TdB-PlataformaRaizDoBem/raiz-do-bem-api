package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.AtualizarAtendimentoDTO;
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

    public Atendimento buscarPeloCpf(String cpf){
        return find("cpf", cpf).firstResult();
    }

    public void atualizar(String cpf, AtualizarAtendimentoDTO request) {
        update("prontuario = ?1, id_colaborador = ?2, data_final = ?3 where cpf = ?4",
                request.getProntuario(), request.getIdColaborador(), request.getDataFinal(), cpf);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
