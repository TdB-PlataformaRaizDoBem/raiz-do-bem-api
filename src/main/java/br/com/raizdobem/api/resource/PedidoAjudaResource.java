package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtualizarPedidoAjudaDTO;
import br.com.raizdobem.api.dto.request.CriarPedidoAjudaDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.service.PedidoAjudaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@RequestScoped
@Path("/pedido-ajuda")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Pedido Ajuda", description = "Disponibiliza funcionalidades relacionadas aos pedidos de ajuda.")
public class PedidoAjudaResource {
    @Inject
    PedidoAjudaService service;

    @GET
    public List<PedidoAjuda> listarTodos(){
        return service.listarTodos();
    }

    @GET
    @Path("/data/{data}")
    public List<PedidoAjuda> listarPorData(@PathParam("data") String data) {
        return service.listarPorData(LocalDate.parse(data));
    }

    @POST
    public Response criar(CriarPedidoAjudaDTO request){
        PedidoAjuda pedidoAjuda = service.criar(request);
        if(pedidoAjuda == null){
            throw new RequisicaoInvalidaException("Não foi possível criar o pedido de ajuda. Dados inválidos.");
        }
        return Response.status(Response.Status.CREATED).entity(pedidoAjuda).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") long id, @RequestBody AtualizarPedidoAjudaDTO dto){
        PedidoAjuda pedido = service.processarPedido(id, dto);
        return Response.ok(pedido).build();
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id){
        boolean apagado = service.excluir(id);

        if(apagado){
            return Response.noContent().build();
        }
        throw new NaoEncontradoException("Pedido de ajuda não encontrado para exclusão.");
    }
}
