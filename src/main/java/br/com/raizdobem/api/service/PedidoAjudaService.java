package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.model.PedidoAjuda;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PedidoAjudaService {
    @Inject
    PedidoAjudaRepository repository;

    public List<PedidoAjuda> listarTodos() {
        return repository.listarTodos();
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
