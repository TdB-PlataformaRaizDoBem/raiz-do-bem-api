package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.Atendimento;
import br.com.raizdobem.api.repository.AtendimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AtendimentoService {
    @Inject
    AtendimentoRepository repository;

    public Atendimento buscar() {
        return new Atendimento();
    }
}
