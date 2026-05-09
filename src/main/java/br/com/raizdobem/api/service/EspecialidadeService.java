package br.com.raizdobem.api.service;

import br.com.raizdobem.api.entity.Especialidade;
import br.com.raizdobem.api.repository.EspecialidadeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EspecialidadeService {
    @Inject
    EspecialidadeRepository repository;

    public List<Especialidade> listarEspecialidades() {
        return repository.listarTodas();
    }

    public Especialidade buscarPorId(Long id) {
        return repository.findById(id);
    }
}
