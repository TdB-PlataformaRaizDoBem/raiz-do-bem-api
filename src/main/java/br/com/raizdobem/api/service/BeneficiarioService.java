package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.AtualizarBeneficiarioDTO;
import br.com.raizdobem.api.dto.CriarBeneficiarioDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.model.Beneficiario;
import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.model.PedidoAjuda;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import br.com.raizdobem.api.repository.EnderecoRepository;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
import br.com.raizdobem.api.repository.ProgramaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    @Inject
    PedidoAjudaRepository pedidoRepository;

    @Inject
    EnderecoRepository enderecoRepository;

    @Inject
    ProgramaRepository programaRepository;


    @Transactional
    public Beneficiario criarBeneficiario(CriarBeneficiarioDTO dto) {
        Beneficiario beneficiario = new Beneficiario();

        String cpf = dto.getCpf();
        if(ValidacaoService.validarCpf(cpf))
            beneficiario.setCpf(cpf);
        else
            throw new ValidacaoException("CPF inserido é inválido");

        PedidoAjuda pedido = pedidoRepository.buscarPorCpf(dto.getCpf());
        if(beneficiario.getCpf().equals(pedido.getCpf())){
            beneficiario.setNomeCompleto(pedido.getNomeCompleto());
            beneficiario.setDataNascimento(pedido.getDataNascimento());
            beneficiario.setTelefone(pedido.getTelefone());
            beneficiario.setEmail(pedido.getEmail());
            beneficiario.setEndereco(pedido.getEndereco());
        }
        beneficiario.setNomeCompleto(dto.getNomeCompleto());
        beneficiario.setDataNascimento(dto.getDataNascimento());
        beneficiario.setTelefone(dto.getTelefone());
        beneficiario.setEmail(dto.getEmail());


        //idEndereco
        //idProgramaSocial

        repository.criar(beneficiario);
        return beneficiario;
    }

    public Beneficiario buscarPorCpf(String cpf) {
        return repository.buscarPorCpf(cpf);
    }

    public List<Beneficiario> listarTodos() {
        return repository.listarTodos();
    }

    @Transactional
    public Beneficiario atualizar(String cpf, AtualizarBeneficiarioDTO request) {
        return repository.atualizar(cpf, request);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }
}
