package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.Colaborador;
import br.com.raizdobem.api.service.ColaboradorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("/colaborador")
@Tag(name = "Colaborador", description = "Disponibiliza funcionalidades relacionadas a colaboradores.")
public class ColaboradorController {
    @Inject
    ColaboradorService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Colaborador> listarTodos(){
        return new ArrayList<>();
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
    public String excluir(@PathParam("cpf") String cpf){
        return "Apagar colaborador";
    }
}
