package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.dto.AtendimentoDTO;
import br.com.raizdobem.api.repository.AtendimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AtendimentoService {
    @Inject
    AtendimentoRepository repository;

    public AtendimentoDTO buscar() {
        return new AtendimentoDTO();
    }
    public List<AtendimentoDTO> listarAtendimentos(){
        return repository.listarTodos();
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
