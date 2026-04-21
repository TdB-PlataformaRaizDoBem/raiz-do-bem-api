package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.Dentista;
import br.com.raizdobem.api.service.DentistaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("/dentista")
@Tag(name = "Dentista", description = "Disponibiliza funcionalidades relacionadas a dentistas.")
public class DentistaController {
    @Inject
    DentistaService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Dentista> listarTodos(){
        return new ArrayList<>();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criando novo dentista";
    }

    @PUT
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("cpf") String cpf){
        return "Atualizando dentista";
    }

    @DELETE
    @Path("/{cpf}")
    @Produces(MediaType.APPLICATION_JSON)
    public String excluir(@PathParam("cpf") String cpf){
        return "Apagar colaborador";
    }
}
