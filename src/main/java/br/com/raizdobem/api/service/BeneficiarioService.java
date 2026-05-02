package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.dto.BeneficiarioDTO;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    public BeneficiarioDTO buscarPorCpf() {
        return new BeneficiarioDTO();
    }

    public List<BeneficiarioDTO> listarTodos() {
        return repository.listarTodos();
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
