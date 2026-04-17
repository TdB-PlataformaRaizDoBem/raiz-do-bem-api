package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.Endereco;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

@Path("/endereco")
@Tag(name = "Endereco", description = "Disponibiliza funcionalidades relacionadas aos endereços.")
public class EnderecoController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Endereco> listarTodos(){
        return new ArrayList<>();
    }

    @GET
    @Path("/{cidade}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Endereco> listarPorCidade(@PathParam("cidade") String cidade){
        return new ArrayList<>();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criar novo endereço";
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("id") int id){
        return "Atualizando Endereço";
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String excluir(@PathParam("id") int id){
        return "Apagar endereço";
    }
}
