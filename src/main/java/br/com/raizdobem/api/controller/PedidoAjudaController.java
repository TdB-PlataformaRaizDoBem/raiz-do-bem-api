package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.PedidoAjuda;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/pedido-ajuda")
public class PedidoAjudaController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PedidoAjuda> listarTodos(){
        return new ArrayList<>();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criando novo pedido de ajuda";
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("id") int id){
        return "Atualizando pedido de ajuda";
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String excluir(@PathParam("id") int id){
        return "Apagar pedido de ajuda";
    }
}
