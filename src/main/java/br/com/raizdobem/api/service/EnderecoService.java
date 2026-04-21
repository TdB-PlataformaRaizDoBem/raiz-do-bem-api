package br.com.raizdobem.api.service;

//import br.com.raizdobem.api.repository.EnderecoRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.model.TipoEndereco;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

import static org.yaml.snakeyaml.nodes.Tag.STR;

@ApplicationScoped
public class EnderecoService {
//    @Inject
//    EnderecoRepository repository;
//
//    public void criar(Endereco endereco){
//        if(endereco != null)
//            repository.adicionar(endereco);
//        else{
//            throw new RuntimeException("Endereço Inválido");
//        }
//    }
//
//    public Endereco buscaPorId(int id){
//        return repository.buscarPorId();
//    }
//    public List<Endereco> listarTodos(){
//        return repository.listarTodos();
//    }
//
//    public List<Endereco> listarPorCidade(String cidade){
//        return repository.listarPorCidade(cidade);
//    }
//
//    public void atualizar(int id, Endereco enderecoAtualizado) {
//        Endereco endereco = repository.buscarPorId(id);
//
//        if(endereco == null){
//            throw new RuntimeException("Endereço não encontrado!!!");
//        }
//        repository.atualizar(id, enderecoAtualizado);
//    }
//
//    public void excluir(int id) {
//        Endereco endereco = repository.buscarPorId(id);
//
//        if(endereco == null){
//            throw new RuntimeException("Endereço não encontrado!!!");
//        }
//        repository.excluir(id);
//    }
//
    public Endereco validarEndereco(String cep, String numero, TipoEndereco tipoEndereco) {
        String enderecoResponse = buscarApiViaCep(cep);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(enderecoResponse, JsonObject.class);

        Endereco endereco = gson.fromJson(enderecoResponse, Endereco.class);
        endereco.cidade = jsonObject.get("cidade").getAsString();

        return endereco;
    }

    public String buscarApiViaCep(String cep) {
       String url = "https://viacep.com.br/ws/%s/json/".formatted(cep);
       if(validarCep(cep)){
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
       }
       else{
           return "CEP Inválido!!! Digite 8 dígitos!";
       }
    }
//
    public boolean validarCep(String cep){
        return cep != null && cep.matches("\\d{8}");
    }
//
//    public boolean validarTipoEndereco(int opc){
//        if (opc != 1 && opc != 2) {
//            throw new RuntimeException("Tipo de endereço inválido! Escolha 1 para Residencial ou 2 para Profissional.");
//        }
//        return true;
//    }
}
