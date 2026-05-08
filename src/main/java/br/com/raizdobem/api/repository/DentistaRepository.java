package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.AtualizarDentistaDTO;
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

    public Dentista buscarPorCpf(String cpf){
        return find("cpf", cpf).firstResult();
    }

    public Dentista buscarPorId(long id){
        return findById(id);
    }

    public List<Dentista> listarPorCidade(String cidade){
        return list("cidade = ?1", cidade);
    }

    public Dentista atualizar(String cpf, AtualizarDentistaDTO request) {
        return find("cpf", cpf).firstResult();
    }
    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
