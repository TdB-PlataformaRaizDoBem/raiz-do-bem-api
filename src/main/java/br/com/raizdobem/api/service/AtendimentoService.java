package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.AtualizarAtendimentoDTO;
import br.com.raizdobem.api.dto.CriarAtendimentoDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.model.Atendimento;
import br.com.raizdobem.api.repository.AtendimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AtendimentoService {
    @Inject
    AtendimentoRepository repository;

    @Transactional
    public Atendimento criarAtendimento(CriarAtendimentoDTO dto){
        Atendimento atendimento = new Atendimento();

        atendimento.setProntuario(dto.getProntuario());

//        atendimento.setBeneficiario();
//        atendimento.setDentista();

        repository.criar(atendimento);
        return atendimento;
    }

    public Atendimento buscarPorCpf(String cpf) {
        return repository.buscarPeloCpf(cpf);
    }

    public List<Atendimento> listarAtendimentos(){
        return repository.listarTodos();
    }

    public void atualizarAtendimento(String cpf, AtualizarAtendimentoDTO request){
        Atendimento atendimentoEncontrado = repository.buscarPeloCpf(cpf);
        if(atendimentoEncontrado == null){
            throw new NaoEncontradoException("Atendimento não encontrado");
        }
        repository.atualizar(cpf, request);
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
