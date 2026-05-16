package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.external.ViaCepDTO;
import br.com.raizdobem.api.dto.request.EntradaEnderecoCompletoDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.service.EnderecoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@RequestScoped
@Path("/endereco")
@Tag(name = "Endereco", description = "Disponibiliza funcionalidades relacionadas aos endereços.")
@Produces(MediaType.APPLICATION_JSON)
public class EnderecoResource {
    @Inject
    EnderecoService service;

    @POST
    @Operation(summary = "Endpoint para a criação de endereços.")
    public Response criar(EntradaEnderecoCompletoDTO request){
        Endereco endereco = service.criar(request);
        if(endereco.getTipoEndereco() == null){
            throw new NaoEncontradoException("Tipo de endereço inválido.");
        }
        if(request.cep().isEmpty()){
            throw new NaoEncontradoException("CEP não encontrado.");
        }
        return Response.status(Response.Status.CREATED).entity(endereco).build();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de todos os endereços.")
    public Response listarTodos(){
        List<Endereco> enderecos = service.listarTodos();
        if(enderecos == null || enderecos.isEmpty()){
            throw new NaoEncontradoException("Nenhum pedido de ajuda encontrado.");
        }
        return Response.ok(enderecos).build();
    }

    @GET
    @Operation(summary = "Endpoint para a listagem de endereços por cidade.")
    @Path("/{cidade}")
    public Response listarPorCidade(@PathParam("cidade") String cidade){
        List<Endereco> enderecos = service.listarPorCidades(cidade);
        if(enderecos == null || enderecos.isEmpty()){
            throw new NaoEncontradoException("Nenhum pedido de ajuda encontrado.");
        }
        return Response.ok(enderecos).build();
    }

    @GET
    @Operation(summary = "Endpoint para buscar endereço específico pelo id.")
    @Path("/id/{id}")
    public Response buscarEndereco(@PathParam("id") Long id){
        Endereco endereco = service.buscaPorId(id);
        return Response.ok(endereco).build();
    }

    @GET
    @Operation(summary = "Endpoint para buscar endereços na API do ViaCep.")
    @Path("/viacep/{cep}")
    public Response buscarViaCep(@PathParam("cep") String cep){
        ViaCepDTO responseViaCep = service.buscarEndereco(cep);
        if(responseViaCep == null){
            throw new NaoEncontradoException("Endereço não enconrado na Api do ViaCep.");
        }
        return Response.ok(responseViaCep).build();
    }

    @PUT
    @Operation(summary = "Endpoint criado para atualizar endereços.")
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, @RequestBody EntradaEnderecoCompletoDTO request){
        Endereco endereco = service.atualizarEndereco(id, request);
        return Response.ok().entity(endereco).build();
    }

    @DELETE
    @Operation(summary = "Endpoint para apagar endereços.")
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        boolean apagado = service.excluir(id);

        if(apagado)
            return Response.noContent().build();

        throw new NaoEncontradoException("Endereço não encontrado.");
    }
}
