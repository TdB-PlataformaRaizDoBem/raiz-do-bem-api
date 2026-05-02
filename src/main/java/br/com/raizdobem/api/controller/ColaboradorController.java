package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.dto.ColaboradorDTO;
import br.com.raizdobem.api.service.ColaboradorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Collections;
import java.util.List;

@RequestScoped
@Path("/colaborador")
@Tag(name = "ColaboradorDTO", description = "Disponibiliza funcionalidades relacionadas a colaboradores.")
public class ColaboradorController {
    @Inject
    ColaboradorService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ColaboradorDTO> listarTodos(){
        return service.listarTodos();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criando novo colaborador";
    }

    @PUT
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("cpf") String cpf){
        return "Atualizando colaborador";
    }

    @DELETE
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluir(@PathParam("cpf") String cpf){
        long retornoDelete = service.excluir(cpf);

        if(retornoDelete == 0){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Collections.singletonMap("erro", "Colaborador não encontrado."))
                    .build();
        }
        return Response.noContent().build();
    }
}
