package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarAtendimentoDTO;
import br.com.raizdobem.api.dto.request.CriarAtendimentoDTO;
import br.com.raizdobem.api.dto.response.AtendimentoDTO;
import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.entity.Atendimento;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.repository.AtendimentoRepository;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.raizdobem.api.mapper.AtendimentoMapper.mapeamentoAtendimento;
import static br.com.raizdobem.api.mapper.AtendimentoMapper.mapeamentoAtendimentos;

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

    @Inject
    BeneficiarioRepository beneficiarioRepository;

    @Inject
    AtendimentoMatchService atendimentoMatchService;

    @Transactional
    public AtendimentoDTO criarAtendimento(CriarAtendimentoDTO dto){
        BeneficiarioDTO beneficiarioDTO = beneficiarioService.buscarPorCpf(dto.cpfBeneficiario());
        if(beneficiarioDTO == null)
            throw new NaoEncontradoException("Beneficiário não foi encontrado.");

        Beneficiario beneficiario = beneficiarioRepository.buscarPorCpf(dto.cpfBeneficiario());
        DentistaDTO dentistaDTO = atendimentoMatchService.melhorMatchDentista(beneficiarioDTO);

        Dentista dentista = dentistaService.buscarEntidadePorId(dentistaDTO.id());
        if(dentista == null)
            throw new NaoEncontradoException("Dentista não foi encontrado.");

        Atendimento atendimento = new Atendimento();

        atendimento.setProntuario(dto.prontuario());
        atendimento.setDataInicial(LocalDate.now());
        atendimento.setBeneficiario(beneficiario);
        atendimento.setDentista(dentista);

        repository.criar(atendimento);
        return mapeamentoAtendimento(atendimento);
    }

    @Transactional
    public AtendimentoDTO buscarPorCpf(String cpf) {
        Atendimento atendimento = repository.buscarPeloCpf(cpf);
        return mapeamentoAtendimento(atendimento);
    }

    @Transactional
    public List<AtendimentoDTO> listarAtendimentos(){
        List<Atendimento> atendimentos = repository.listarTodos();
        return mapeamentoAtendimentos(atendimentos);
    }

    @Transactional
    public void encerrarAtendimento(String cpf, AtualizarAtendimentoDTO dto){
        Atendimento atendimento = repository.buscarPeloCpf(cpf);
        if(atendimento == null)
            throw new NaoEncontradoException("Atendimento não encontrado");

        if(dto.prontuario() == null)
            throw new NaoEncontradoException("Prontuário inválido, não foi possível atualizar atendimento.");
        atendimento.setProntuario(dto.prontuario());

        if(dto.idColaborador() == null)
            throw new NaoEncontradoException("Id de colaborador inválido.");

        Colaborador colaborador = colaboradorService.buscarPorId(dto.idColaborador());
        if (colaborador == null)
            throw new NaoEncontradoException("Colaborador inválido, não foi possível atualizar atendimento.");

        atendimento.setColaborador(colaborador);

        atendimento.setDataFinal(LocalDate.now());
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }

    @Transactional
    public List<AtendimentoDTO> listarParaExportacao() {
        return listarAtendimentos().stream()
                .map(a -> new AtendimentoDTO(
                        a.id(),
                        a.prontuario(),
                        a.beneficiario() != null ? a.beneficiario() : "BENEFICIÁRIO NÃO ENCONTRADO",
                        a.dentista() != null ? a.dentista() : "DENTISTA NÃO ENCONTRADO",
                        a.dataInicial(),
                        a.dataFim() != null ? a.dataFim() : "NAO FINALIZADO"
                ))
                .collect(Collectors.toList());
    }
}
