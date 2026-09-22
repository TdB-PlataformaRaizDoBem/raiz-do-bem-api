package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.entity.Atendimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
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

    public List<Atendimento> listagemPaginacao(int pagina, int tamanho){
        return findAll().page(Page.of(pagina, tamanho)).list();
    }

    public Atendimento buscarPeloCpf(String cpf){
        return find("beneficiario.cpf", cpf).firstResult();
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
