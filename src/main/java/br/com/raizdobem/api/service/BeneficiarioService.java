package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.BeneficiarioUpdateRequest;
import br.com.raizdobem.api.dto.request.BeneficiarioCreateRequest;
import br.com.raizdobem.api.dto.response.BeneficiarioResponse;
import br.com.raizdobem.api.entity.*;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.mapper.BeneficiarioMapper;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import br.com.raizdobem.api.util.CpfValidatorUtil;
import io.quarkus.cache.CacheResult;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

import static br.com.raizdobem.api.mapper.BeneficiarioMapper.mapeamentoBeneficiario;
import static br.com.raizdobem.api.mapper.BeneficiarioMapper.mapeamentoBeneficiarios;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    @Inject
    PedidoAjudaService pedidoAjudaService;

    @Inject
    ProgramaService programaService;

    @Inject
    EnderecoService enderecoService;

    @Transactional
    public BeneficiarioResponse criarBeneficiario(BeneficiarioCreateRequest dto) {
        Beneficiario beneficiario = new Beneficiario();
        if(dto == null)
            throw new RequisicaoInvalidaException("Inserção de beneficiário inválida.");

        PedidoAjuda pedido = pedidoAjudaService.buscarEntidadePorId(dto.idPedidoAjuda());
        if(pedido == null)
            throw new NaoEncontradoException("Não foi possível encontrar pedido informado.");

        if(pedido.getStatus() != StatusPedido.APROVADO)
            throw new RequisicaoInvalidaException("Pedido de ajuda não foi APROVADO. Impossível seguir o processo de criação de beneficiário.");

        beneficiario.setCpf(pedido.getCpf());
        beneficiario.setNomeCompleto(pedido.getNomeCompleto());
        beneficiario.setDataNascimento(pedido.getDataNascimento());
        beneficiario.setTelefone(pedido.getTelefone());
        beneficiario.setEmail(pedido.getEmail());
        beneficiario.setEndereco(pedido.getEndereco());
        beneficiario.setPedido(pedido);

        ProgramaSocial programaSocial = programaService.buscarPorId(dto.idProgramaSocial());
        if(programaSocial == null)
            throw new NaoEncontradoException("Programa social não encontrado.");
        else{
            beneficiario.setProgramaSocial(programaSocial);
        }

        repository.criar(beneficiario);
        return mapeamentoBeneficiario(beneficiario);
    }

    public BeneficiarioResponse buscarPorCpf(String cpf) {
        if(!CpfValidatorUtil.cpfValido(cpf))
            throw new ValidacaoException("CPF inválido.");

        Beneficiario beneficiario = repository.buscarPorCpf(cpf);
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não encontrado.");
        return mapeamentoBeneficiario(beneficiario);
    }

    public BeneficiarioResponse buscarPorId(Long id) {
        Beneficiario beneficiario = repository.buscarPorId(id);
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não encontrado.");
        return mapeamentoBeneficiario(beneficiario);
    }

    @CacheResult(cacheName = "beneficiarios")
    public List<BeneficiarioResponse> listarTodos() {
        return BeneficiarioMapper.mapeamentoBeneficiarios(repository.listarTodos());
    }
    @CacheResult(cacheName = "beneficiarios")
    public List<BeneficiarioResponse> listarTodosPaginacao(int pagina, int tamanho) {
        return BeneficiarioMapper.mapeamentoBeneficiarios(repository.listarTodos(pagina, tamanho));
    }

    public List<BeneficiarioResponse> listarPorCidade(String cidade) {
        List <Beneficiario> beneficiarios = repository.listarPorCidade(cidade);
        return mapeamentoBeneficiarios(beneficiarios);
    }

    public List<BeneficiarioResponse> listarPorPrograma(long idProgramaSocial) {
        List <Beneficiario> beneficiarios = repository.listarPorPrograma(idProgramaSocial);
        return mapeamentoBeneficiarios(beneficiarios);
    }

    @Transactional
    public BeneficiarioResponse atualizar(String cpf, BeneficiarioUpdateRequest request) {
        Beneficiario beneficiario = repository.atualizar(cpf, request);
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não encontrado, CPF inválido.");

        beneficiario.setTelefone(request.telefone());
        beneficiario.setEmail(request.email());
        enderecoService.entradaEndereco(beneficiario.getEndereco(), request.endereco(), TipoEndereco.RESIDENCIAL);

        return mapeamentoBeneficiario(beneficiario);
    }

    @Transactional
    public boolean excluir(String cpf) {
        if(!CpfValidatorUtil.cpfValido(cpf)){
            throw new NaoEncontradoException("CPF inválido.");
        }
        long exclusao = repository.excluir(cpf);
        return exclusao > 0;
    }
}
