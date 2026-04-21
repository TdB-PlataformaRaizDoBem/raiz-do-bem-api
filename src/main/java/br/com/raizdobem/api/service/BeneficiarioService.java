package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.Beneficiario;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    public Beneficiario buscarPorCpf() {
        return new Beneficiario();
    }
}
