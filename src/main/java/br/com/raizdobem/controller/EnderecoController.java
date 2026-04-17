package br.com.raizdobem.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/endereco")
public class EnderecoController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listarTodos(){
        return "Listando todos os endereços";
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
