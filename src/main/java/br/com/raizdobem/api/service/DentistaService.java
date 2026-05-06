package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.DentistaRequestCriacaoDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.model.Dentista;
import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.model.Sexo;
import br.com.raizdobem.api.repository.DentistaRepository;
import br.com.raizdobem.api.repository.EnderecoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class DentistaService {
    @Inject
    DentistaRepository repository;
    @Inject
    EnderecoRepository enderecoRepository;

    @Transactional
    public Dentista criarDentista(DentistaRequestCriacaoDTO request){
        Dentista dentista = new Dentista();

        String croEntrada = request.getCroDentista();
        String cpfEntrada = request.getCpf();
        String sexoEntrada = request.getSexo().toUpperCase();

        if(validarCro(croEntrada))
            dentista.setCroDentista(request.getCroDentista());
        else
            throw new ValidacaoException("CRO inserido é inválido");

        if(ValidacaoService.validarCpf(cpfEntrada))
            dentista.setCpf(cpfEntrada);
        else
            throw new ValidacaoException("CPF inserido é inválido");

        dentista.setNomeCompleto(request.getNomeCompleto());
        dentista.setSexo(Sexo.valueOf(sexoEntrada));
        dentista.setTelefone(request.getTelefone());
        dentista.setEmail(request.getEmail());
        dentista.setCategoria(request.getCategoria());
        Endereco endereco = enderecoRepository.buscarPeloId(request.getId_endereco());
        if(endereco == null)
            throw new NaoEncontradoException("Endereço não encontrado!");
        else
            dentista.setEndereco(endereco);

        dentista.setDisponivel(request.getDisponivel());

        repository.criar(dentista);
        return dentista;
    }

    public List<Dentista> listarTodos() {
        return repository.listarTodos();
    }

    public List<Dentista> listarPorCidades(String cidade) {
        return repository.listarPorCidade(cidade);
    }

    @Transactional
    public Dentista atualizar(String cpf) {
        return repository.atualizar(cpf);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }

    public boolean validarCro(String cro){
        return cro!=null && cro.matches("^[a-zA-Z]{2,}\\d{2}$");
    }
}
