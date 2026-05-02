package br.com.raizdobem.api.controller;

import br.com.raizdobem.api.model.dto.BeneficiarioDTO;
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
@Tag(name = "BeneficiarioDTO", description = "Disponibiliza funcionalidades relacionadas aos beneficiários.")
public class BeneficiarioController {
    @Inject
    BeneficiarioService service;

    @GET
    @Operation(summary = "Endpoint de listagem dos beneficiários cadastrados.")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BeneficiarioDTO> listarTodos(){
        return service.listarTodos();
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
    public BeneficiarioDTO buscarPorCpf(@PathParam("cpf") String cpf){
        return service.buscarPorCpf();
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
    public Response excluir(@PathParam("cpf") String cpf){
//        boolean apagado = service.excluir(cpf);
//
//        if(apagado){
//            return Response.noContent().build();
//        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Collections.singletonMap("erro", "Beneficiário não encontrado."))
                .build();
    }
}
