package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.Colaborador;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ColaboradorService {
    @Inject
    ColaboradorRepository repository;

    public List<Colaborador> listarTodos() {
        return repository.listarTodos();
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
