package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.AtualizarDentistaDTO;
import br.com.raizdobem.api.dto.CriarDentistaDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.Sexo;
import br.com.raizdobem.api.repository.DentistaRepository;
import br.com.raizdobem.api.repository.EnderecoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class DentistaService {
    @Inject
    DentistaRepository repository;
    @Inject
    EnderecoRepository enderecoRepository;

    @Transactional
    public Dentista criarDentista(@Valid CriarDentistaDTO dto){
        Dentista dentista = new Dentista();

        String cpfEntrada = dto.getCpf();
        String sexoEntrada = dto.getSexo().toUpperCase();

        if(ValidacaoService.validarCpf(cpfEntrada))
            dentista.setCpf(cpfEntrada);
        else
            throw new ValidacaoException("CPF inserido é inválido");

        dentista.setCroDentista(dto.getCroDentista());
        dentista.setNomeCompleto(dto.getNomeCompleto());
        dentista.setSexo(Sexo.valueOf(sexoEntrada));
        dentista.setTelefone(dto.getTelefone());
        dentista.setEmail(dto.getEmail());
        dentista.setCategoria(dto.getCategoria());
        Endereco endereco = enderecoRepository.buscarPeloId(dto.getId_endereco());
        if(endereco == null)
            throw new NaoEncontradoException("Endereço não encontrado!");
        else
            dentista.setEndereco(endereco);

        dentista.setDisponivel(dto.getDisponivel());

        repository.criar(dentista);
        return dentista;
    }

    public List<Dentista> listarTodos() {
        return repository.listarTodos();
    }

    public Dentista exibirDentista(String cpf) {
        return repository.buscarPorCpf(cpf);
    }

    public List<Dentista> listarPorCidades(String cidade) {
        return repository.listarPorCidade(cidade);
    }

    @Transactional
    public Dentista atualizar(String cpf, @Valid AtualizarDentistaDTO request) {
        return repository.atualizar(cpf, request);
    }

    @Transactional
    public long excluir(String cpf) {
        return repository.excluir(cpf);
    }

//    public boolean validarCro(String cro){
//        return cro!=null && cro.matches("^[a-zA-Z]{2,}\\d{2}$");
//    }
}
