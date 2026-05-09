package br.com.raizdobem.api.service;

import br.com.raizdobem.api.client.ViaCepClient;
import br.com.raizdobem.api.dto.request.EnderecoRequestDTO;
import br.com.raizdobem.api.dto.external.ViaCepDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RegraNegocioException;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.repository.EnderecoRepository;
import br.com.raizdobem.api.entity.TipoEndereco;

import java.util.List;

import com.google.gson.Gson;
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
    public Endereco criar(EnderecoRequestDTO dto) {
        String cep = dto.getCep();

        if(validarCep(cep)){
            ViaCepDTO viaCep = buscarEndereco(cep);
            Endereco endereco = new Endereco();

            endereco.setCep(cep);
            endereco.setLogradouro(viaCep.getLogradouro());
            endereco.setBairro(viaCep.getBairro());
            endereco.setCidade(viaCep.getLocalidade());
            endereco.setEstado(viaCep.getUf());
            endereco.setNumero(dto.getNumero());

            TipoEndereco tipoEndereco = null;

            if (dto.getTipoEndereco().equalsIgnoreCase("RESIDENCIAL")) {
                tipoEndereco = TipoEndereco.RESIDENCIAL;
            } else if(dto.getTipoEndereco().equalsIgnoreCase("PROFISSIONAL")){
                tipoEndereco = TipoEndereco.PROFISSIONAL;
            }

            endereco.setTipoEndereco(tipoEndereco);

            repository.criar(endereco);
            return endereco;
        }
        throw new RegraNegocioException("CEP inválido! Insira 8 dígitos!");
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

//    public Endereco validarEndereco(String cep, String numero, TipoEndereco tipoEndereco) {
//        String enderecoResponse = buscarApiViaCep(cep);
//        Gson gson = new Gson();
//        JsonObject jsonObject = gson.fromJson(enderecoResponse, JsonObject.class);
//
//        Endereco endereco = gson.fromJson(enderecoResponse, Endereco.class);
//        endereco.setCidade(jsonObject.get("cidade").getAsString());
//
//        return endereco;
//    }

    public ViaCepDTO buscarEndereco(String cep) {
        return client.buscarEndereco(cep);
    }

    public boolean validarCep(String cep) {
        return cep != null && cep.matches("\\d{8}");
    }

    @Transactional
    public Endereco atualizarEndereco(Long id, EnderecoRequestDTO dto) {
        Endereco endereco = buscaPorId(id);
        if(endereco == null)
            throw new NaoEncontradoException("Endereço não encontrado.");

        endereco.setCep(dto.getCep());

        ViaCepDTO viaCep = buscarEndereco(endereco.getCep());

        endereco.setLogradouro(viaCep.getLogradouro());
        endereco.setBairro(viaCep.getBairro());
        endereco.setCidade(viaCep.getLocalidade());
        endereco.setEstado(viaCep.getUf());

        TipoEndereco tipoEndereco = null;

        if (dto.getTipoEndereco().equalsIgnoreCase("RESIDENCIAL")) {
            tipoEndereco = TipoEndereco.RESIDENCIAL;
        } else if(dto.getTipoEndereco().equalsIgnoreCase("PROFISSIONAL")){
            tipoEndereco = TipoEndereco.PROFISSIONAL;
        }
        endereco.setTipoEndereco(tipoEndereco);
        endereco.setNumero(dto.getNumero());

        repository.persist(endereco);
        return endereco;
    }

    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
