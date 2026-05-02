package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.dto.PedidoAjudaDTO;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PedidoAjudaService {
    @Inject
    PedidoAjudaRepository repository;

    public List<PedidoAjudaDTO> listarTodos() {
        return repository.listarTodos();
    }

    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
