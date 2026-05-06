package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.ColaboradorRequestDTO;
import br.com.raizdobem.api.model.Colaborador;
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
    public Colaborador criarColaborador(ColaboradorRequestDTO request) {
        Colaborador colaborador = new Colaborador();

        if(ValidacaoService.validarCpf(request.getCpf())){
            colaborador.setCpf(request.getCpf());
        }

        colaborador.setNomeCompleto(request.getNomeCompleto());
        colaborador.setDataNascimento(request.getDataNascimento());
        colaborador.setDataContratacao(request.getDataContratacao());
        colaborador.setEmail(request.getEmail());

//        Colaborador colaborador = bo.validarColaborador(cpf, nome, dataNascimento, dataContratacao, email);

        repository.criar(colaborador);
        return colaborador;
    }

    public List<Colaborador> listarTodos() {
        return repository.listarTodos();
    }

    @Transactional
    public Colaborador atualizarColaborador(String cpf) {
        return repository.atualizar(cpf);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
