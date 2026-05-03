package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.PedidoAjuda;
import br.com.raizdobem.api.service.PedidoAjudaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

@RequestScoped
@Path("/pedido-ajuda")
@Tag(name = "Pedido Ajuda", description = "Disponibiliza funcionalidades relacionadas aos pedidos de ajuda.")
public class PedidoAjudaController {
    @Inject
    PedidoAjudaService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PedidoAjuda> listarTodos(){
        return service.listarTodos();
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
    public Response excluir(@PathParam("id") Long id){
        boolean apagado = service.excluir(id);

        if(apagado){
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Collections.singletonMap("erro", "Pedido de ajuda não encontrado."))
                .build();
    }
}
