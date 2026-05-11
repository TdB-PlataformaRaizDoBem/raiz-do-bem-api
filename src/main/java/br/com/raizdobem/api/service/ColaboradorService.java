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
        colaborador.setCpf(dto.cpf());

        colaborador.setNomeCompleto(dto.nomeCompleto());
        colaborador.setDataNascimento(dto.dataNascimento());
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
