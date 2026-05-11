package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtualizarDentistaDTO;
import br.com.raizdobem.api.dto.request.CriarDentistaDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.service.DentistaService;
import br.com.raizdobem.api.util.CsvUtil;
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
        DentistaDTO dentista = service.criarDentista(request);
        if(dentista == null){
            throw new RequisicaoInvalidaException("Dados de dentista inválidos.");
        }
        return Response.status(Response.Status.CREATED).entity(dentista).build();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de todos os dentistas.")
    public Response listarTodos(){
        List<DentistaDTO> dentistas = service.listarTodos();
        if(dentistas == null)
            throw new NaoEncontradoException("Lista de dentistas vazia.");
        return Response.status(200).entity(dentistas).build();
    }

    @GET
    @Path("/disponiveis")
    @Operation(summary = "Endpoint para a listagem de dentistas disponíveis.")
    public Response listarDisponiveis() {
        List<DentistaDTO> dentistas = service.listarDisponiveis();
        if(dentistas == null)
            throw new NaoEncontradoException("Lista de dentistas disponíveis vazia.");
        return Response.status(200).entity(dentistas).build();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a listagem de um único dentista.")
    public DentistaDTO exibirDentista(@PathParam("cpf") String cpf){
        return service.exibirDentista(cpf);
    }

    @GET
    @Path("/cidade/{cidade}")
    @Operation(summary = "Endpoint para a listagem de todos os dentistas de uma cidade específica.")
    public Response listarTodos(@PathParam("cidade") String cidade){
        List<DentistaDTO> dentistas = service.listarPorCidades(cidade);
        if(dentistas == null || dentistas.isEmpty())
            throw new NaoEncontradoException("Lista de dentistas não foi encontrada.");
        return Response.status(200).entity(dentistas).build();
    }

    @GET
    @Path("/exportarCsv")
    @Produces("text/csv")
    @Operation(summary = "Endpoint para a exportar todos os dentistas em arquivo csv.")
    public Response exportarCsv(){
        List<DentistaDTO> lista = service.listarParaExportacao();

        String csv = CsvUtil.gerarCsvDentistas(lista);

        return Response.ok(csv).header("Content-Disposition",
                "attachment; filename=dentistas.csv")
                .build();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para a atualização de dentista.")
    public Response atualizar(@PathParam("cpf") String cpf, @Valid @RequestBody AtualizarDentistaDTO request){
        DentistaDTO dentista = service.atualizar(cpf, request);
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
