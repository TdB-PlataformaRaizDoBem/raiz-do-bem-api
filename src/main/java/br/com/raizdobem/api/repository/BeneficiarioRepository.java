package br.com.raizdobem.api.repository;


import br.com.raizdobem.api.model.Beneficiario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class BeneficiarioRepository implements PanacheRepository<Beneficiario> {

    public List<Beneficiario> listarTodos(){
        return listAll();
    }
}
