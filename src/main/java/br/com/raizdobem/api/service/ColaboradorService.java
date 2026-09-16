package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.ColaboradorUpdateRequest;
import br.com.raizdobem.api.dto.request.ColaboradorCreateRequest;
import br.com.raizdobem.api.dto.response.ColaboradorResponse;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.mapper.ColaboradorMapper;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import br.com.raizdobem.api.util.CpfValidatorUtil;
import io.quarkus.elytron.security.common.BcryptUtil;
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
    public ColaboradorResponse criarColaborador(ColaboradorCreateRequest dto) {
        if(!CpfValidatorUtil.cpfValido(dto.cpf())){
            throw new ValidacaoException("Cpf inválido");
        }
        Colaborador colaborador = new Colaborador();

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
        colaborador.setSenha(
                BcryptUtil.bcryptHash(dto.senha())
        );
        colaborador.setRole(dto.role());

        repository.criar(colaborador);

        return ColaboradorMapper.mapeamentoParaResponse(colaborador);
    }

    public List<ColaboradorResponse> listarTodos() {
        return ColaboradorMapper.mapeamentoParaResponse(repository.listarTodos());
    }

    public ColaboradorResponse exibirColaboradorPorCpf(String cpf) {
        Colaborador colaborador = repository.buscarPorCpf(cpf);
        if(colaborador == null)
            throw new NaoEncontradoException("Colaborador não foi encontrado!");
        return ColaboradorMapper.mapeamentoParaResponse(colaborador);
    }

    public Colaborador buscarPorId(Long id){
        return repository.buscarPorId(id);
    }

    @Transactional
    public void atualizarColaborador(String cpf, ColaboradorUpdateRequest dto) {
        Colaborador colaboradorEncontrado = repository.buscarPorCpf(cpf);
        if(colaboradorEncontrado == null){
            throw new NaoEncontradoException("Colaborador não encontrado");
        }
        String novoEmail = (dto.email() != null && !dto.email().isBlank())
                ? dto.email()
                : colaboradorEncontrado.getEmail();

        String novaSenha = (dto.senha() != null && !dto.senha().isBlank())
                ? BcryptUtil.bcryptHash(dto.senha())
                : colaboradorEncontrado.getSenha();

        ColaboradorUpdateRequest dtoValido = new ColaboradorUpdateRequest(novoEmail, novaSenha);
        repository.atualizar(cpf, dtoValido);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
