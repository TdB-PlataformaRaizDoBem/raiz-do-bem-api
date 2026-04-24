package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.Dentista;
import br.com.raizdobem.api.repository.DentistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class DentistaService {
    @Inject
    DentistaRepository repository;

    public List<Dentista> listarTodos() {
        return repository.listarTodos();
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
