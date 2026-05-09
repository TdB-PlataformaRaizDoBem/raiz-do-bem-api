package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.AtualizarAtendimentoDTO;
import br.com.raizdobem.api.dto.CriarAtendimentoDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.entity.Atendimento;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.repository.AtendimentoRepository;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import br.com.raizdobem.api.repository.DentistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AtendimentoService {
    @Inject
    AtendimentoRepository repository;

    @Inject
    BeneficiarioRepository beneficiarioRepository;

    @Inject
    DentistaRepository dentistaRepository;

    @Transactional
    public Atendimento criarAtendimento(CriarAtendimentoDTO dto){
        Atendimento atendimento = new Atendimento();

        Beneficiario beneficiario = beneficiarioRepository.buscarPorId(dto.getIdBeneficiario());
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não foi encontrado.");

        Dentista dentista = dentistaRepository.buscarPorId(dto.getIdDentista());
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não foi encontrado.");

        atendimento.setProntuario(dto.getProntuario());
        atendimento.setDataInicial(dto.getDataInicial());
        atendimento.setBeneficiario(beneficiario);
        atendimento.setDentista(dentista);

        repository.criar(atendimento);
        return atendimento;
    }

    public Atendimento buscarPorCpf(String cpf) {
        return repository.buscarPeloCpf(cpf);
    }

    public List<Atendimento> listarAtendimentos(){
        return repository.listarTodos();
    }

    @Transactional
    public void encerrarAtendimento(String cpf, AtualizarAtendimentoDTO request){
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
