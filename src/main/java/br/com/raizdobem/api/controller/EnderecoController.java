package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.service.EnderecoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("/endereco")
@Tag(name = "Endereco", description = "Disponibiliza funcionalidades relacionadas aos endereços.")
public class EnderecoController {
    @Inject
    EnderecoService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Endereco> listarTodos(){
        return new ArrayList<>();
    }

    @GET
    @Path("/cidade/{cidade}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Endereco> listarPorCidade(@PathParam("cidade") String cidade){
        return new ArrayList<>();
    }

    @POST
    @Operation(summary = "Endpoint para a criação de endereços.")
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criar novo endereço";
    }

//    @GET
//    @Operation(summary = "Endpoint para a busca de endereços.")
//    @Path("/cep/{cep}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Endereco buscarEndereco(@PathParam("cep") String cep){
//        return new Endereco();
//    }

    @GET
    @Operation(summary = "Endpoint para buscar endereços na API do ViaCep.")
    @Path("/viacep/{cep}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarViaCep(@PathParam("cep") String cep){
        String responseViaCep = service.buscarApiViaCep(cep);
        if(responseViaCep.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Endereço não encontrado!").build();
        }
        return Response.ok(responseViaCep).build();
    }

    @PUT
    @Operation(summary = "Endpoint criado para atualizar endereços.")
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("id") Long id){
        return "Atualizando Endereço";
    }

    @DELETE
    @Operation(summary = "Endpoint para apagar endereços.")
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String excluir(@PathParam("id") int id){
        return "Apagar endereço";
    }
}
