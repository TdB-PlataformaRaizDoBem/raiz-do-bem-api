package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Dentista;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DentistaRepository implements PanacheRepository<Dentista> {

    public void criar(Dentista dentista){
        persist(dentista);
    }

    public List<Dentista> listarTodos(){
        return listAll();
    }

    public List<Dentista> listarPorCidade(String cidade){
        return list("cidade = ?1", cidade);
    }

    public Dentista atualizar(String cpf) {
        return find("cpf", cpf).firstResult();
    }
    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
