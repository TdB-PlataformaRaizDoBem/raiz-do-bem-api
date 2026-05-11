package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarBeneficiarioDTO;
import br.com.raizdobem.api.dto.request.CriarBeneficiarioDTO;
import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.entity.StatusPedido;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

import static br.com.raizdobem.api.mapper.BeneficiarioMapper.mapeamentoDTO;
import static br.com.raizdobem.api.mapper.BeneficiarioMapper.mapeamentoListaDTO;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    @Inject
    PedidoAjudaService pedidoAjudaService;

    @Inject
    ProgramaService programaService;


    @Transactional
    public BeneficiarioDTO criarBeneficiario(CriarBeneficiarioDTO dto) {
        Beneficiario beneficiario = new Beneficiario();
        if(dto == null)
            throw new RequisicaoInvalidaException("Inserção de beneficiário inválida.");

        if(ValidacaoService.validarCpf(dto.cpf()))
            beneficiario.setCpf(dto.cpf());
        else
            throw new ValidacaoException("CPF inserido é inválido");

        PedidoAjuda pedido = pedidoAjudaService.buscarPorCpf(dto.cpf());
        if(pedido == null)
            throw new NaoEncontradoException("Não foi possível encontrar pedido informado.");

        if(pedido.getStatus() != StatusPedido.APROVADO)
            throw new RequisicaoInvalidaException("Pedido de ajuda não foi APROVADO. Impossível seguir o processo de criação de beneficiário.");

        if(beneficiario.getCpf().equals(pedido.getCpf())){
            beneficiario.setNomeCompleto(pedido.getNomeCompleto());
            beneficiario.setDataNascimento(pedido.getDataNascimento());
            beneficiario.setTelefone(pedido.getTelefone());
            beneficiario.setEmail(pedido.getEmail());
            beneficiario.setEndereco(pedido.getEndereco());
            beneficiario.setPedido(pedido);
        }

        ProgramaSocial programaSocial = programaService.buscarPorId(dto.idProgramaSocial());
        if(programaSocial == null)
            throw new NaoEncontradoException("Programa social não encontrado.");
        else{
            beneficiario.setProgramaSocial(programaSocial);
        }

        repository.criar(beneficiario);
        return mapeamentoDTO(beneficiario);
    }

    public BeneficiarioDTO buscarPorCpf(String cpf) {
        if(!ValidacaoService.validarCpf(cpf))
            throw new ValidacaoException("CPF inválido.");

        Beneficiario beneficiario = repository.buscarPorCpf(cpf);
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não encontrado.");
        return mapeamentoDTO(beneficiario);
    }

    public BeneficiarioDTO buscarPorId(Long id) {
        Beneficiario beneficiario = repository.buscarPorId(id);
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não encontrado.");
        return mapeamentoDTO(beneficiario);
    }

    public List<BeneficiarioDTO> listarTodos() {
        List <Beneficiario> beneficiarios = repository.listarTodos();
        return mapeamentoListaDTO(beneficiarios);
    }

    public List<BeneficiarioDTO> listarPorCidade(String cidade) {
        List <Beneficiario> beneficiarios = repository.listarPorCidade(cidade);
        return mapeamentoListaDTO(beneficiarios);
    }

    public List<BeneficiarioDTO> listarPorPrograma(long idProgramaSocial) {
        List <Beneficiario> beneficiarios = repository.listarPorPrograma(idProgramaSocial);
        return mapeamentoListaDTO(beneficiarios);
    }

    @Transactional
    public BeneficiarioDTO atualizar(String cpf, AtualizarBeneficiarioDTO request) {
        Beneficiario beneficiario = repository.atualizar(cpf, request);
        if(beneficiario == null)
            throw new NaoEncontradoException("Beneficiário não encontrado, CPF inválido.");

        return mapeamentoDTO(beneficiario);
    }

    @Transactional
    public boolean excluir(String cpf) {
        if(!ValidacaoService.validarCpf(cpf)){
            throw new NaoEncontradoException("CPF inválido.");
        }
        long exclusao = repository.excluir(cpf);
        return exclusao > 0;
    }
}
