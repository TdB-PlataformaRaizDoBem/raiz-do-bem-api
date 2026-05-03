package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.dto.DentistaRequestCriacaoDTO;
import br.com.raizdobem.api.model.Dentista;
import br.com.raizdobem.api.service.DentistaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

@RequestScoped
@Path("/dentista")
@Tag(name = "Dentista", description = "Disponibiliza funcionalidades relacionadas a dentistas.")
@Produces(MediaType.APPLICATION_JSON)
public class DentistaController {
    @Inject
    DentistaService service;

    @GET
    @Operation(summary = "Endpoint para a listagem de todos os dentistas.")
    public List<Dentista> listarTodos(){
        return service.listarTodos();
    }

    @POST
    @Operation(summary = "Endpoint para a criação de dentista.")
    public Response criar(DentistaRequestCriacaoDTO request){
        Dentista dentista = service.criarDentista(request);
        return Response.status(Response.Status.CREATED).entity(dentista).build();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a atualização de dentista.")
    public Response atualizar(@PathParam("cpf") String cpf){
        Dentista dentista = service.atualizar(cpf);
        return Response.status(Response.Status.OK).entity(dentista).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a exclusão de dentista.")
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
