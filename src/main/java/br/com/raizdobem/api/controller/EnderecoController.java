package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.dto.EnderecoRequestDTO;
import br.com.raizdobem.api.model.Endereco;
import br.com.raizdobem.api.service.EnderecoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

@RequestScoped
@Path("/endereco")
@Tag(name = "Endereco", description = "Disponibiliza funcionalidades relacionadas aos endereços.")
@Produces(MediaType.APPLICATION_JSON)
public class EnderecoController {
    @Inject
    EnderecoService service;

    @GET
    @Operation(summary = "Endpoint para a listagem de todos os endereços.")
    public List<Endereco> listarTodos(){
        return service.listarTodos();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de endereços por cidade.")
    @Path("/{cidade}")
    public List<Endereco> listarPorCidade(@PathParam("cidade") String cidade){
        return service.listarPorCidades(cidade);
    }

    @POST
    @Operation(summary = "Endpoint para a criação de endereços.")
    @APIResponse(responseCode = "201", description = "Endereço criado com sucesso")
    @APIResponse(responseCode = "400", description = "Dados de endereço inválidos")
    @APIResponse(responseCode = "422", description = "Regra de negócio inválida")
    public Response criar(EnderecoRequestDTO request){

        if(request.getTipoEndereco() == null || request.getTipoEndereco().isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Collections.singletonMap("erro", "Tipo de endereço inválido."))
                    .build();
        }
        if(request.getCep().isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Collections.singletonMap("erro", "CEP inválido."))
                    .build();
        }

        Endereco enderecoCompleto = service.criar(request);
        return Response.status(Response.Status.CREATED).entity(enderecoCompleto).build();
    }

    @GET
    @Operation(summary = "Endpoint para buscar endereço específico pelo id.")
    @Path("/id/{id}")
    public Endereco buscarEndereco(@PathParam("id") Long id){
        return service.buscaPorId(id);
    }

    @GET
    @Operation(summary = "Endpoint para buscar endereços na API do ViaCep.")
    @Path("/viacep/{cep}")
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
