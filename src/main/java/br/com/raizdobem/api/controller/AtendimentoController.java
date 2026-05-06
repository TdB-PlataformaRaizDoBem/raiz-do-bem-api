package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.dto.CriarAtendimentoDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.model.Atendimento;
import br.com.raizdobem.api.service.AtendimentoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

@RequestScoped
@Path("/atendimento")
@Tag(name = "Atendimento", description = "Disponibiliza funcionalidades relacionadas aos atendimentos.")
@Produces(MediaType.APPLICATION_JSON)
public class AtendimentoController {
    @Inject
    AtendimentoService service;
    
    @GET
    public List<Atendimento> listarTodos(){
        return service.listarAtendimentos();
    }

    @POST
    public Response criar(CriarAtendimentoDTO request){
        Atendimento atendimento = service.criarAtendimento(request);
        if(atendimento == null){
            throw new RequisicaoInvalidaException("Não foi possível criar o atendimento. Dados inválidos.");
        }
        return Response.status(Response.Status.CREATED).entity(atendimento).build();
    }

    @GET
    @Path("/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf){
        try{
            Atendimento atendimento = service.buscarPorCpf(cpf);
            return Response.ok(atendimento).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

//    @PUT
//    @Path("/{id}")
//    public String atualizar(@PathParam("id") long id){
//        return service.atualizarAtendimento(cpf, request);
//    }

    @DELETE
    @Path("/{id}")
    public Response excluirAtendimento(@PathParam("id") Long id){
        boolean apagado = service.excluir(id);

        if(apagado){
            return Response.noContent().build();
        }
        throw new NaoEncontradoException("Pedido de ajuda não encontrado para exclusão.");
    }
}
