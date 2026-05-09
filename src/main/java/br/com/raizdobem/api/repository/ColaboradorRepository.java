package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.AtualizarColaboradorDTO;
import br.com.raizdobem.api.entity.Colaborador;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ColaboradorRepository implements PanacheRepository<Colaborador> {

    public void criar(Colaborador colaborador){
        persist(colaborador);
    }

    public List<Colaborador> listarTodos(){
        return listAll();
    }

    public Colaborador buscarPorCpf(String cpf){
        return find("cpf", cpf).firstResult();
    }

    public Colaborador buscarPorId(long id){
        return findById(id);
    }

    public void atualizar(String cpf, AtualizarColaboradorDTO request){
        update("email = ?1 where cpf = ?2", request.getEmail(), cpf);
    }

    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
