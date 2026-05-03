package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.Beneficiario;
import br.com.raizdobem.api.service.BeneficiarioService;
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
@Path("/beneficiario")
@Tag(name = "Beneficiario", description = "Disponibiliza funcionalidades relacionadas aos beneficiários.")
@Produces(MediaType.APPLICATION_JSON)
public class BeneficiarioController {
    @Inject
    BeneficiarioService service;

    @GET
    @Operation(summary = "Endpoint de listagem dos beneficiários cadastrados.")
    public List<Beneficiario> listarTodos(){
        return service.listarTodos();
    }

    @POST
    @Operation(summary = "Endpoint para a criação de beneficiários.")
    public String criar(){
        return "Criando novo beneficiário";
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para encontrar um beneficiário específico.")
    public Beneficiario buscarPorCpf(@PathParam("cpf") String cpf){
        return service.buscarPorCpf();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint de atualização de informações de beneficiário.")
    public Response atualizar(@PathParam("cpf") String cpf){
        Beneficiario beneficiario = service.atualizar(cpf);
        return Response.status(Response.Status.OK).entity(beneficiario).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para apagar beneficiário existente.")
    public Response excluir(@PathParam("cpf") String cpf){
       service.excluir(cpf);
       return Response.noContent().build();
    }
}
