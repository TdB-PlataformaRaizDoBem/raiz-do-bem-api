package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.Beneficiario;
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
    public Beneficiario atualizar(String cpf) {
        return repository.atualizar(cpf);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
