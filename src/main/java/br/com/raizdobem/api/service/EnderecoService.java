package br.com.raizdobem.api.service;

import br.com.raizdobem.api.client.ViaCepClient;
import br.com.raizdobem.api.dto.request.EntradaEnderecoDTO;
import br.com.raizdobem.api.dto.external.ViaCepDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RegraNegocioException;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.repository.EnderecoRepository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class EnderecoService {
    @Inject
    EnderecoRepository repository;

    @Inject
    @RestClient
    ViaCepClient client;

    @Transactional
    public Endereco criar(EntradaEnderecoDTO dto) {
        if(!validarCep(dto.cep())){
            throw new RegraNegocioException("CEP inválido! Insira 8 dígitos!");
        }
        Endereco endereco = new Endereco();

        entradaEndereco(endereco, dto);
        repository.criar(endereco);
        return endereco;
}

    public Endereco buscaPorId(Long id){
        return repository.buscarPeloId(id);
    }

    public List<Endereco> listarTodos() {
        return repository.listarTodos();
    }

    public List<Endereco> listarPorCidades(String cidade) {
        return repository.listarPorCidade(cidade);
    }

    public ViaCepDTO buscarEndereco(String cep) {
        return client.buscarEndereco(cep);
    }

    public static boolean validarCep(String cep) {
        return cep != null && cep.matches("\\d{8}");
    }

    @Transactional
    public Endereco atualizarEndereco(Long id, EntradaEnderecoDTO dto) {
        Endereco endereco = buscaPorId(id);
        if(endereco == null)
            throw new NaoEncontradoException("Endereço não encontrado.");

        entradaEndereco(endereco, dto);

        repository.persist(endereco);
        return endereco;
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }

    public void entradaEndereco(Endereco endereco, EntradaEnderecoDTO dto){
        ViaCepDTO viaCep = client.buscarEndereco(dto.cep());
        if(viaCep == null){
             throw new RequisicaoInvalidaException("Requisição ViaCep inválida.");
        }
        if("true".equals(viaCep.erro()) ){
            throw new NaoEncontradoException("Endereço Inválido. CEP não encontrado");
        }
        endereco.setCep(dto.cep());
        endereco.setLogradouro(viaCep.logradouro());
        endereco.setNumero(dto.numero());
        endereco.setBairro(viaCep.bairro());
        endereco.setCidade(viaCep.cidade());
        endereco.setEstado(viaCep.uf());
    }
}