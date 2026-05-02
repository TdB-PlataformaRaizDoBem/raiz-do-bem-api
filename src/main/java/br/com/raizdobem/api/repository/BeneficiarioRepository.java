package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.model.dto.BeneficiarioDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class BeneficiarioRepository implements PanacheRepository<BeneficiarioDTO> {

    public List<BeneficiarioDTO> listarTodos(){
        return listAll();
    }

    public void criar(BeneficiarioDTO beneficiarioDTO){
        persist(beneficiarioDTO);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}
