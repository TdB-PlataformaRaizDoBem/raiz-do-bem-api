package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarColaboradorDTO;
import br.com.raizdobem.api.dto.request.CriarColaboradorDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ColaboradorService {
    @Inject
    ColaboradorRepository repository;

    @Transactional
    public Colaborador criarColaborador(CriarColaboradorDTO dto) {
        Colaborador colaborador = new Colaborador();

        if (!ValidacaoService.validarCpf(dto.cpf())) {
            throw new ValidacaoException("CPF inserido é inválido");
        }
        Colaborador colaboradorExistente = repository.buscarPorCpf(dto.cpf());
        if(colaboradorExistente != null){
            throw new ValidacaoException("Já existe um colaborador com esse CPF");
        }

        colaborador.setCpf(dto.cpf());
        if(dto.nomeCompleto() == null || dto.nomeCompleto().isBlank())
            throw new NaoEncontradoException("Nome completo deve ser inserido.");
        colaborador.setNomeCompleto(dto.nomeCompleto());

        if(dto.dataNascimento() == null || dto.dataNascimento().isAfter(LocalDate.now()))
            throw new NaoEncontradoException("Data de nascimento inválida.");
        colaborador.setDataNascimento(dto.dataNascimento());

        if(dto.dataContratacao() == null || dto.dataContratacao().isAfter(LocalDate.now()) )
            throw new NaoEncontradoException("Data de contratação inválida.");
        colaborador.setDataContratacao(dto.dataContratacao());
        colaborador.setEmail(dto.email());

        repository.criar(colaborador);
        return colaborador;
    }

    public List<Colaborador> listarTodos() {
        return repository.listarTodos();
    }
    public Colaborador exibirColaborador(String cpf) {
        return repository.buscarPorCpf(cpf);
    }

    public Colaborador buscarPorId(Long id){
        return repository.buscarPorId(id);
    }

    @Transactional
    public void atualizarColaborador(String cpf, AtualizarColaboradorDTO dto) {
        Colaborador colaboradorEncontrado = repository.buscarPorCpf(cpf);
        if(colaboradorEncontrado == null){
            throw new NaoEncontradoException("Colaborador não encontrado");
        }
        repository.atualizar(cpf, dto);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
