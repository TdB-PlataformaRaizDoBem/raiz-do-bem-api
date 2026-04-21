package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.Beneficiario;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import br.com.raizdobem.api.service.BeneficiarioService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

@RequestScoped
@Path("/beneficiario")
@Tag(name = "Beneficiario", description = "Disponibiliza funcionalidades relacionadas aos beneficiários.")
public class BeneficiarioController {
    @Inject
    BeneficiarioService service;

    @GET
    @Operation(summary = "Endpoint de listagem dos beneficiários cadastrados.")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Beneficiario> listarTodos(){
        return new ArrayList<>();
    }

    @POST
    @Operation(summary = "Endpoint para a criação de beneficiários.")
    @Produces(MediaType.APPLICATION_JSON)
    public String criar(){
        return "Criando novo beneficiário";
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para encontrar um beneficiário específico.")
    @Produces(MediaType.APPLICATION_JSON)
    public Beneficiario buscarPorCpf(@PathParam("cpf") String cpf){
        return new Beneficiario();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint de atualização de informações de beneficiário.")
    @Produces(MediaType.APPLICATION_JSON)
    public String atualizar(@PathParam("cpf") String cpf){
        return "Atualizando beneficiário";
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para apagar beneficiário existente.")
    @Produces(MediaType.APPLICATION_JSON)
    public String excluir(@PathParam("cpf") String cpf){
        return "Apagar beneficiário";
    }
}
