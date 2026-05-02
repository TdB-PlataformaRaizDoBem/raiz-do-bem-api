package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.dto.EnderecoDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class EnderecoRepository implements PanacheRepository<EnderecoDTO> {

//    public EnderecoDTO buscarPorCep(String cep){
//        return find("cep", cep).firstResult();
//    }

    public void criar(EnderecoDTO enderecoDTO){
        persist(enderecoDTO);
    }

    public List<EnderecoDTO> listarTodos(){
        return listAll();
    }

    public List<EnderecoDTO> listarPorCidade(String cidade){
        return list("cidade = ?1", cidade);
    }

    public EnderecoDTO buscarPeloId(Long id){
        return findById(id);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
