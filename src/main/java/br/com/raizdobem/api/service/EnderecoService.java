package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.EnderecoRequestDTO;
import br.com.raizdobem.api.dto.ResponseViaCepDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RegraNegocioException;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.repository.EnderecoRepository;
import br.com.raizdobem.api.entity.TipoEndereco;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EnderecoService {
    @Inject
    EnderecoRepository repository;

    @Transactional
    public Endereco criar(EnderecoRequestDTO dto) {
        Gson gson = new Gson();
        String cep = dto.getCep();

        if(validarCep(cep)){
            String response = buscarApiViaCep(cep);

            ResponseViaCepDTO viaCep = gson.fromJson(response, ResponseViaCepDTO.class);

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

    public String buscarApiViaCep(String cep) {
        String url = "https://viacep.com.br/ws/%s/json/".formatted(cep);
        if (validarCep(cep)) {
            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                return response.body();
            } catch (Exception e) {
                throw new RuntimeException("Erro ao consultar Api: " + e.getMessage());
            }
        } else {
            return "CEP Inválido!!! Digite 8 dígitos!";
        }
    }

    public boolean validarCep(String cep) {
        return cep != null && cep.matches("\\d{8}");
    }

    @Transactional
    public Endereco atualizarEndereco(Long id, EnderecoRequestDTO dto) {
        Gson gson = new Gson();
        Endereco endereco = buscaPorId(id);
        if(endereco == null)
            throw new NaoEncontradoException("Endereço não encontrado.");

        endereco.setCep(dto.getCep());

        String response = buscarApiViaCep(endereco.getCep());
        ResponseViaCepDTO viaCep = gson.fromJson(response, ResponseViaCepDTO.class);

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
