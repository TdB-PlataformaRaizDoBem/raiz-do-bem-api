package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.Beneficiario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BeneficiarioRepository implements PanacheRepository<Beneficiario> {

    public List<Beneficiario> listarTodos(){
        return listAll();
    }

    public void criar(Beneficiario beneficiario){
        persist(beneficiario);
    }

    public Beneficiario atualizar(String cpf){
        return find("cpf", cpf).firstResult();
    }

    public long excluir(String cpf) {
        return delete("cpf", cpf);
    }
}
