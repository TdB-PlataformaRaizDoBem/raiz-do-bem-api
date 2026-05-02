package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.dto.DentistaDTO;
import br.com.raizdobem.api.repository.DentistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class DentistaService {
    @Inject
    DentistaRepository repository;

    public List<DentistaDTO> listarTodos() {
        return repository.listarTodos();
    }

    public List<DentistaDTO> listarPorCidades(String cidade) {
        return repository.listarPorCidade(cidade);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
