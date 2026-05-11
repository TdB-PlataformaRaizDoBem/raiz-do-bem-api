package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtualizarColaboradorDTO;
import br.com.raizdobem.api.dto.request.CriarColaboradorDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.service.ColaboradorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@RequestScoped
@Path("/colaborador")
@Tag(name = "Colaborador", description = "Disponibiliza funcionalidades relacionadas a colaboradores.")
@Produces(MediaType.APPLICATION_JSON)
public class ColaboradorResource {
    @Inject
    ColaboradorService service;

    @POST
    @Operation(summary = "Endpoint para a criação de colaborador.")
    public Response criar(@Valid CriarColaboradorDTO request){
        Colaborador colaborador = service.criarColaborador(request);
        if(colaborador == null){
            throw new NaoEncontradoException("Dados de colaborador inválidos.");
        }
        return Response.status(Response.Status.CREATED).entity(colaborador).build();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de colaboradores.")
    public Response listarTodos(){
        List<Colaborador> colaboradores = service.listarTodos();
        if(colaboradores == null || colaboradores.isEmpty())
            throw new NaoEncontradoException("Nenhum colaborador encontrado.");
        return Response.ok(colaboradores).build();
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
    public Response atualizar(@PathParam("cpf") String cpf, @Valid @RequestBody AtualizarColaboradorDTO dto){
        service.atualizarColaborador(cpf, dto);
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
