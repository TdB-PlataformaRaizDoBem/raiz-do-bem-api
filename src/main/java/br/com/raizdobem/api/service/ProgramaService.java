package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.ProgramaSocial;
import br.com.raizdobem.api.repository.ProgramaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ProgramaService {
    @Inject
    ProgramaRepository repository;

    public List<ProgramaSocial> listarProgramasSociais() {
        return repository.listarTodos();
    }
}
