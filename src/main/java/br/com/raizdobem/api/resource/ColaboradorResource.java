package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.ColaboradorUpdateRequest;
import br.com.raizdobem.api.dto.request.ColaboradorCreateRequest;
import br.com.raizdobem.api.dto.response.ColaboradorResponse;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.service.ColaboradorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@RequestScoped
@Path("/colaborador")
@Tag(name = "Colaborador", description = "Disponibiliza funcionalidades relacionadas a colaboradores.")
@Produces(MediaType.APPLICATION_JSON)
public class ColaboradorResource {
    @Inject
    ColaboradorService service;

    @POST
    @Operation(summary = "Endpoint de criação de um colaborador da ONG.")
    @RolesAllowed("ADMIN")
    public Response criarColaborador(@Valid ColaboradorCreateRequest request){
        ColaboradorResponse colaborador = service.criarColaborador(request);
        if(colaborador == null){
            throw new NaoEncontradoException("Dados de colaborador inválidos.");
        }
        return Response.status(Response.Status.CREATED).entity(colaborador).build();
    }

    @GET
    @Operation(summary = "Endpoint de listagem de todos os colaboradores.")
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response listarTodos(){
        return Response.ok(service.listarTodos()).build();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint que exibe um colaborador único usando o CPF.")
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response buscarColaborador(@PathParam("cpf") String cpf){
        return Response.ok(service.exibirColaboradorPorCpf(cpf)).build();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint de atualização de email do colaborador.")
    @RolesAllowed("ADMIN")
    public Response atualizar(@PathParam("cpf") String cpf, @Valid @RequestBody ColaboradorUpdateRequest dto){
        service.atualizarColaborador(cpf, dto);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint de exclusão de um colaborador.")
    @RolesAllowed("ADMIN")
    public Response excluir(@PathParam("cpf") String cpf){
        long excluido = service.excluir(cpf);
        if(excluido == 0){
            throw new NaoEncontradoException("Colaborador não encontrado.");
        }
        return Response.noContent().build();
    }
}
