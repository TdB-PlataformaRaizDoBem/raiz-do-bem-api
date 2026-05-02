package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.dto.EnderecoDTO;
import br.com.raizdobem.api.service.EnderecoService;
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
@Path("/endereco")
@Tag(name = "EnderecoDTO", description = "Disponibiliza funcionalidades relacionadas aos endereços.")
public class EnderecoController {
    @Inject
    EnderecoService service;

    @GET
    @Operation(summary = "Endpoint para a listagem de todos os endereços.")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EnderecoDTO> listarTodos(){
        return service.listarTodos();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de endereços por cidade.")
    @Path("/{cidade}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EnderecoDTO> listarPorCidade(@PathParam("cidade") String cidade){
        return service.listarPorCidades(cidade);
    }

    @POST
    @Operation(summary = "Endpoint para a criação de endereços.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response criar(@PathParam("cep") String cep, @PathParam("numero") String numero, @PathParam("tipoEndereco") String tipoLocal){
        tipoLocal = tipoLocal.toUpperCase();
        EnderecoDTO enderecoDTOCompleto = service.criar(cep, numero, tipoLocal);
        return Response.status(Response.Status.CREATED).entity(enderecoDTOCompleto).build();
    }

    @GET
    @Operation(summary = "Endpoint para buscar endereço específico pelo id.")
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public EnderecoDTO buscarEndereco(@PathParam("id") Long id){
        return service.buscaPorId(id);
    }

    @GET
    @Operation(summary = "Endpoint para buscar endereços na API do ViaCep.")
    @Path("/viacep/{cep}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarViaCep(@PathParam("cep") String cep){
        String responseViaCep = service.buscarApiViaCep(cep);
        if(responseViaCep.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Endereço não encontrado!").build();
        }
        return Response.ok(responseViaCep).build();
    }

    @PUT
    @Operation(summary = "Endpoint criado para atualizar endereços.")
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizar(@PathParam("id") Long id){
        boolean responseAtualizacao = service.atualizarEndereco(id);

        if(responseAtualizacao){
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Endereço não encontrado")
                .build();

    }

    @DELETE
    @Operation(summary = "Endpoint para apagar endereços.")
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluir(@PathParam("id") Long id) {
        boolean apagado = service.excluir(id);

        if(apagado){
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Collections.singletonMap("erro", "Endereço não encontrado."))
                .build();
    }
}
