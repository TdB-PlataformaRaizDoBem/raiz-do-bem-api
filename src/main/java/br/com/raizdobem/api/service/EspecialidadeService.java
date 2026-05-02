package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.dto.EspecialidadeDTO;
import br.com.raizdobem.api.repository.EspecialidadeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EspecialidadeService {
    @Inject
    EspecialidadeRepository repository;

    public List<EspecialidadeDTO> listarEspecialidades() {
        return repository.listarTodas();
    }
}
