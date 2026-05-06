package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.AtualizarBeneficiarioDTO;
import br.com.raizdobem.api.dto.CriarBeneficiarioDTO;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.model.Beneficiario;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BeneficiarioService {
    @Inject
    BeneficiarioRepository repository;

    @Transactional
    public Beneficiario criarBeneficiario(CriarBeneficiarioDTO dto) {
        Beneficiario beneficiario = new Beneficiario();

        String cpf = dto.getCpf();
        if(ValidacaoService.validarCpf(cpf))
            beneficiario.setCpf(cpf);
        else
            throw new ValidacaoException("CPF inserido é inválido");

        beneficiario.setNomeCompleto(dto.getNomeCompleto());
        beneficiario.setDataNascimento(dto.getDataNascimento());
        beneficiario.setTelefone(dto.getTelefone());
        beneficiario.setEmail(dto.getEmail());
        //idPedido
        //idEndereco
        //idProgramaSocial - vem do pedido

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
