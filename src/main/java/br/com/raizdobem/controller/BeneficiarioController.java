package br.com.raizdobem.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/beneficiario")
public class BeneficiarioController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listarTodos(){
        return "Listando todos pedidos";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criando novo pedido de ajuda";
    }

    @PUT
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("cpf") String cpf){
        return "Atualizando pedido de ajuda";
    }

    @DELETE
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public String excluir(@PathParam("cpf") String cpf){
        return "Apagar pedido de ajuda";
    }
}
