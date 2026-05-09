package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtualizarDentistaDTO;
import br.com.raizdobem.api.dto.request.CriarDentistaDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.service.DentistaService;
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
@Path("/dentista")
@Tag(name = "Dentista", description = "Disponibiliza funcionalidades relacionadas a dentistas.")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DentistaResource {
    @Inject
    DentistaService service;

    @POST
    @Operation(summary = "Endpoint para a criação de dentista.")
    public Response criar(@Valid CriarDentistaDTO request){
        Dentista dentista = service.criarDentista(request);
        if(dentista == null){
            throw new RequisicaoInvalidaException("Dados de dentista inválidos.");
        }
        return Response.status(Response.Status.CREATED).entity(dentista).build();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de todos os dentistas.")
    public List<Dentista> listarTodos(){
        return service.listarTodos();
    }

    @GET
    @Path("/disponiveis")
    @Operation(summary = "Endpoint para a listagem de dentistas disponíveis.")
    public List<Dentista> listarDisponiveis() {
        return service.listarDisponiveis();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a listagem de um único dentista.")
    public Dentista exibirDentista(@PathParam("cpf") String cpf){
        return service.exibirDentista(cpf);
    }

    @GET
    @Path("/cidade/{cidade}")
    @Operation(summary = "Endpoint para a listagem de todos os dentistas de uma cidade específica.")
    public List<Dentista> listarTodos(@PathParam("cidade") String cidade){
        return service.listarPorCidades(cidade);
    }


    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a atualização de dentista.")
    public Response atualizar(@PathParam("cpf") String cpf, @RequestBody AtualizarDentistaDTO request){
        Dentista dentista = service.atualizar(cpf, request);
        return Response.status(Response.Status.OK).entity(dentista).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a exclusão de dentista.")
    public Response excluir(@PathParam("cpf") String cpf){
        long responseDelete = service.excluir(cpf);

        if(responseDelete == 0){
            throw new NaoEncontradoException("Dentista não encontrado para exclusão.");
        }
        return Response.noContent().build();
    }
}
