package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarBeneficiarioDTO;
import br.com.raizdobem.api.dto.request.CriarBeneficiarioDTO;
import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    @Inject
    PedidoAjudaService pedidoAjudaService;

    @Inject
    EnderecoService enderecoService;

    @Inject
    ProgramaService programaService;


    @Transactional
    public Beneficiario criarBeneficiario(CriarBeneficiarioDTO dto) {
        Beneficiario beneficiario = new Beneficiario();

        if(ValidacaoService.validarCpf(dto.getCpf()))
            beneficiario.setCpf(dto.getCpf());
        else
            throw new ValidacaoException("CPF inserido é inválido");

        PedidoAjuda pedido = pedidoAjudaService.buscarPorCpf(dto.getCpf());
        if(beneficiario.getCpf().equals(pedido.getCpf())){
            beneficiario.setNomeCompleto(pedido.getNomeCompleto());
            beneficiario.setDataNascimento(pedido.getDataNascimento());
            beneficiario.setTelefone(pedido.getTelefone());
            beneficiario.setEmail(pedido.getEmail());
            beneficiario.setEndereco(pedido.getEndereco());
        }

        ProgramaSocial programaSocial = programaService.buscarPorId(dto.getIdProgramaSocial());
        if(programaSocial == null)
            throw new NaoEncontradoException("Programa social não encontrado.");
        else{
            beneficiario.setProgramaSocial(programaSocial);
        }

        repository.criar(beneficiario);
        return beneficiario;
    }

    public Beneficiario buscarPorCpf(String cpf) {
        return repository.buscarPorCpf(cpf);
    }

    public Beneficiario buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public List<Beneficiario> listarTodos() {
        return repository.listarTodos();
    }

    public List<Beneficiario> listarPorCidade(String cidade) {
        return repository.listarPorCidade(cidade);
    }

    public List<Beneficiario> listarPorPrograma(long idProgramaSocial) {
        return repository.listarPorPrograma(idProgramaSocial);
    }

    @Transactional
    public Beneficiario atualizar(String cpf, AtualizarBeneficiarioDTO request) {
        return repository.atualizar(cpf);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
