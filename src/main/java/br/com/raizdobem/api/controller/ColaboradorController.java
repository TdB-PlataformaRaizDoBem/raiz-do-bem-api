package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.dto.AtualizarColaboradorDTO;
import br.com.raizdobem.api.dto.CriarColaboradorDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.model.Colaborador;
import br.com.raizdobem.api.service.ColaboradorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@RequestScoped
@Path("/colaborador")
@Tag(name = "Colaborador", description = "Disponibiliza funcionalidades relacionadas a colaboradores.")
@Produces(MediaType.APPLICATION_JSON)
public class ColaboradorController {
    @Inject
    ColaboradorService service;

    @POST
    @Operation(summary = "Endpoint para a criação de colaborador.")
    public Response criar(CriarColaboradorDTO request){
        Colaborador colaborador = service.criarColaborador(request);
        if(colaborador == null){
            throw new NaoEncontradoException("Dados de colaborador inválidos.");
        }
        return Response.status(Response.Status.CREATED).entity(colaborador).build();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de colaboradores.")
    public List<Colaborador> listarTodos(){
        return service.listarTodos();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para exibir colaborador único.")
    public Colaborador buscarUnico(@PathParam("cpf") String cpf){
        return service.exibirColaborador(cpf);
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a atualização de colaborador.")
    public Response atualizar(@PathParam("cpf") String cpf, @RequestBody AtualizarColaboradorDTO request){
        service.atualizarColaborador(cpf, request);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a exclusão de colaborador.")
    public Response excluir(@PathParam("cpf") String cpf){
        long excluido = service.excluir(cpf);
        if(excluido == 0){
            throw new NaoEncontradoException("Colaborador não encontrado.");
        }
        return Response.noContent().build();
    }
}
