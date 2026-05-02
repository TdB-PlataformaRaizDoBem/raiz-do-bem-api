package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.dto.DentistaDTO;
import br.com.raizdobem.api.service.DentistaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

@RequestScoped
@Path("/dentista")
@Tag(name = "DentistaDTO", description = "Disponibiliza funcionalidades relacionadas a dentistas.")
public class DentistaController {
    @Inject
    DentistaService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DentistaDTO> listarTodos(){
        return service.listarTodos();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criando novo dentista";
    }

    @PUT
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("cpf") String cpf){
        return "Atualizando dentista";
    }

    @DELETE
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluir(@PathParam("cpf") String cpf){
        long responseDelete = service.excluir(cpf);

        if(responseDelete == 0){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("erro", "Dentista não encontrado."))
                    .build();
        }
        return Response.noContent().build();
    }
}
