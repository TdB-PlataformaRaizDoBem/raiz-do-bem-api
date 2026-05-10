package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarAtendimentoDTO;
import br.com.raizdobem.api.dto.request.CriarAtendimentoDTO;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.entity.Atendimento;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.repository.AtendimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class AtendimentoService {
    @Inject
    AtendimentoRepository repository;

    @Inject
    BeneficiarioService beneficiarioService;

    @Inject
    DentistaService dentistaService;

    @Inject
    ColaboradorService colaboradorService;

    @Transactional
    public Atendimento criarAtendimento(CriarAtendimentoDTO dto){
        Atendimento atendimento = new Atendimento();

        Beneficiario beneficiario = beneficiarioService.buscarPorId(dto.getBeneficiario().getId());
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não foi encontrado.");

        Dentista dentista = dentistaService.buscarPorId(dto.getDentista().getId());
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não foi encontrado.");

        atendimento.setProntuario(dto.getProntuario());
        atendimento.setDataInicial(LocalDate.now());
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
    public void encerrarAtendimento(String cpf, AtualizarAtendimentoDTO dto){
        Atendimento atendimento = repository.buscarPeloCpf(cpf);
        if(atendimento == null)
            throw new NaoEncontradoException("Atendimento não encontrado");


        if(dto.getProntuario() == null)
            throw new NaoEncontradoException("Prontuário inválido, não foi possível atualizar atendimento.");
        atendimento.setProntuario(dto.getProntuario());

        if(dto.getIdColaborador() == null)
            throw new NaoEncontradoException("Id de colaborador inválido.");

        Colaborador colaborador = colaboradorService.buscarPorId(dto.getIdColaborador());
        if (colaborador == null)
            throw new NaoEncontradoException("Colaborador inválido, não foi possível atualizar atendimento.");

        atendimento.setColaborador(colaborador);

        atendimento.setDataFinal(LocalDate.now());
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
