package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Endereco;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EnderecoRepository implements PanacheRepository<Endereco> {

//    public Endereco buscarPorCep(String cep){
//        return find("cep", cep).firstResult();
//    }

    public void criar(Endereco endereco){
        persist(endereco);
    }

    public List<Endereco> listarTodos(){
        return listAll();
    }

    public List<Endereco> listarPorCidade(String cidade){
        return list("cidade = ?1", cidade);
    }

    public Endereco buscarPeloId(Long id){
        return findById(id);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
