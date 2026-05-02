package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.dto.AtendimentoDTO;
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
@Tag(name = "AtendimentoDTO", description = "Disponibiliza funcionalidades relacionadas aos atendimentos.")
public class AtendimentoController {
    @Inject
    AtendimentoService service;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AtendimentoDTO> listarTodos(){
        return service.listarAtendimentos();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criando novo atendimento";
    }

    @GET
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorCpf(@PathParam("cpf") String cpf){
        try{
            AtendimentoDTO atendimentoDTO = service.buscar();
            return Response.ok(atendimentoDTO).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("id") long id){
        return "Atualizando atendimento";
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluirAtendimento(@PathParam("id") Long id){
        boolean apagado = service.excluir(id);

        if(apagado){
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Collections.singletonMap("erro", "Atendimento não encontrado."))
                .build();
    }
}
