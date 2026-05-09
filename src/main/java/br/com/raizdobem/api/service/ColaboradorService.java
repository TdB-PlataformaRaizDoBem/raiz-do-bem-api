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

        if (!ValidacaoService.validarCpf(dto.getCpf())) {
            throw new ValidacaoException("CPF inserido é inválido");
        }
        colaborador.setCpf(dto.getCpf());

        colaborador.setNomeCompleto(dto.getNomeCompleto());
        colaborador.setDataNascimento(dto.getDataNascimento());
        colaborador.setDataContratacao(dto.getDataContratacao());
        colaborador.setEmail(dto.getEmail());

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
    public void atualizarColaborador(String cpf, AtualizarColaboradorDTO request) {
        Colaborador colaboradorEncontrado = repository.buscarPorCpf(cpf);
        if(colaboradorEncontrado == null){
            throw new NaoEncontradoException("Colaborador não encontrado");
        }
        repository.atualizar(cpf, request);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
