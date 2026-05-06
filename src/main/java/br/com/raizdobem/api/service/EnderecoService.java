package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.EnderecoRequestDTO;
import br.com.raizdobem.api.dto.ResponseViaCepDTO;
import br.com.raizdobem.api.exception.RegraNegocioException;
import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.repository.EnderecoRepository;
import br.com.raizdobem.api.model.TipoEndereco;

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

            endereco.setCep(cep); //CEP sem parênteses
            endereco.setLogradouro(viaCep.getLogradouro());
            endereco.setBairro(viaCep.getBairro());
            endereco.setCidade(viaCep.getLocalidade());
            endereco.setEstado(viaCep.getUf());

            endereco.setNumero(dto.getNumero());
            TipoEndereco tipoEndereco = null;

            if (dto.getTipoEndereco().equals("RESIDENCIAL")) {
                tipoEndereco = TipoEndereco.RESIDENCIAL;
            } else if(dto.getTipoEndereco().equals("PROFISSIONAL")){
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

//    public void atualizar(int id, Endereco enderecoAtualizado) {
//        Endereco endereco = repository.buscarPorId(id);
//
//        if(endereco == null){
//            throw new RuntimeException("Endereço não encontrado!!!");
//        }
//        repository.atualizar(id, enderecoAtualizado);
//    }


    public Endereco validarEndereco(String cep, String numero, TipoEndereco tipoEndereco) {
        String enderecoResponse = buscarApiViaCep(cep);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(enderecoResponse, JsonObject.class);

        Endereco endereco = gson.fromJson(enderecoResponse, Endereco.class);
        endereco.setCidade(jsonObject.get("cidade").getAsString());

        return endereco;
    }

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

    //
    public boolean validarCep(String cep) {
        return cep != null && cep.matches("\\d{8}");
    }

    @Transactional
    public boolean atualizarEndereco(Long id) {
        return true;
    }
//
//    public boolean validarTipoEndereco(int opc){
//        if (opc != 1 && opc != 2) {
//            throw new RuntimeException("Tipo de endereço inválido! Escolha 1 para Residencial ou 2 para Profissional.");
//        }
//        return true;
//    }
    @Transactional
    public boolean excluir(Long id) {
        return repository.excluir(id);
    }
}
