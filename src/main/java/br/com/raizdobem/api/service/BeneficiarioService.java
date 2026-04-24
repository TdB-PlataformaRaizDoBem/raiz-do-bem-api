package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.Beneficiario;
import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    public Beneficiario buscarPorCpf() {
        return new Beneficiario();
    }

    public List<Beneficiario> listarTodos() {
        return repository.listarTodos();
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
