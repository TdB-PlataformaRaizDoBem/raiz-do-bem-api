package br.com.raizdobem.api.service;

import br.com.raizdobem.api.model.dto.EnderecoDTO;
import br.com.raizdobem.api.repository.EnderecoRepository;
import br.com.raizdobem.api.model.dto.TipoEndereco;

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
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class EnderecoService {
    @Inject
    EnderecoRepository repository;

    @Transactional
    public EnderecoDTO criar(String cep, String numero, String tipoLocal) {
        Gson gson = new Gson();
        if(validarCep(cep)){
            String response = buscarApiViaCep(cep);
            EnderecoDTO enderecoDTO = gson.fromJson(response, EnderecoDTO.class);

            enderecoDTO.setNumero(numero);
            TipoEndereco tipoEndereco;

            if (tipoLocal.equals("RESIDENCIAL")) {
                tipoEndereco = TipoEndereco.RESIDENCIAL;
            } else {
                tipoEndereco = TipoEndereco.PROFISSIONAL;
            }

            enderecoDTO.setTipoEndereco(tipoEndereco);

            repository.criar(enderecoDTO);
            return enderecoDTO;
        }
        throw new WebApplicationException("CEP inválido! Insira 8 dígitos!");
}

    public EnderecoDTO buscaPorId(Long id){
        return repository.buscarPeloId(id);
    }

    public List<EnderecoDTO> listarTodos() {
        return repository.listarTodos();
    }

    public List<EnderecoDTO> listarPorCidades(String cidade) {
        return repository.listarPorCidade(cidade);
    }

//    public void atualizar(int id, EnderecoDTO enderecoAtualizado) {
//        EnderecoDTO endereco = repository.buscarPorId(id);
//
//        if(endereco == null){
//            throw new RuntimeException("Endereço não encontrado!!!");
//        }
//        repository.atualizar(id, enderecoAtualizado);
//    }


    public EnderecoDTO validarEndereco(String cep, String numero, TipoEndereco tipoEndereco) {
        String enderecoResponse = buscarApiViaCep(cep);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(enderecoResponse, JsonObject.class);

        EnderecoDTO enderecoDTO = gson.fromJson(enderecoResponse, EnderecoDTO.class);
        enderecoDTO.setCidade(jsonObject.get("cidade").getAsString());

        return enderecoDTO;
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
