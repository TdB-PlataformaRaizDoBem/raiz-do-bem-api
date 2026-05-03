package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.dto.ColaboradorRequestDTO;
import br.com.raizdobem.api.model.Colaborador;
import br.com.raizdobem.api.service.ColaboradorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.lang.annotation.Repeatable;
import java.util.Collections;
import java.util.List;

@RequestScoped
@Path("/colaborador")
@Tag(name = "Colaborador", description = "Disponibiliza funcionalidades relacionadas a colaboradores.")
@Produces(MediaType.APPLICATION_JSON)
public class ColaboradorController {
    @Inject
    ColaboradorService service;
    @Inject
    Request request;

    @POST
    @Operation(summary = "Endpoint para a criação de colaborador.")
    public Response criar(ColaboradorRequestDTO request){
        Colaborador colaborador = service.criarColaborador(request);
        return Response.status(Response.Status.CREATED).entity(colaborador).build();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de colaboradores.")
    public List<Colaborador> listarTodos(){
        return service.listarTodos();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a atualização de colaborador.")
    public Response atualizar(@PathParam("cpf") String cpf){
        Colaborador colaboradorAtualizar = service.atualizarColaborador(cpf);
        return Response.status(Response.Status.OK).entity(colaboradorAtualizar).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a exclusão de colaborador.")
    public Response excluir(@PathParam("cpf") String cpf){
        service.excluir(cpf);
        return Response.noContent().build();
    }
}
