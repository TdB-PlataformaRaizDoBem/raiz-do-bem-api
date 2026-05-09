package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.AtualizarBeneficiarioDTO;
import br.com.raizdobem.api.dto.CriarBeneficiarioDTO;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.service.BeneficiarioService;
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
@Path("/beneficiario")
@Tag(name = "Beneficiario", description = "Disponibiliza funcionalidades relacionadas aos beneficiários.")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BeneficiarioResource {
    @Inject
    BeneficiarioService service;

    @GET
    @Operation(summary = "Endpoint de listagem dos beneficiários cadastrados.")
    public List<Beneficiario> listarTodos(){
        return service.listarTodos();
    }

    @POST
    @Operation(summary = "Endpoint para a criação de beneficiários.")
    public Response criar(CriarBeneficiarioDTO request){
        Beneficiario beneficiario = service.criarBeneficiario(request);
        if(beneficiario == null){
            throw new RequisicaoInvalidaException("Dados de beneficiário inválidos.");
        }
        return Response.status(Response.Status.CREATED).entity(beneficiario).build();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para encontrar um beneficiário específico.")
    public Beneficiario buscarPorCpf(@PathParam("cpf") String cpf){
        return service.buscarPorCpf(cpf);
    }

    @GET
    @Path("/cidade/{cidade}")
    @Operation(summary = "Endpoint para listar beneficiários por cidade.")
    public List<Beneficiario> listarPorCidade(@PathParam("cidade") String cidade) {
        return service.listarPorCidade(cidade);
    }

    @GET
    @Path("/programa/{idProgramaSocial}")
    @Operation(summary = "Endpoint para listar beneficiários por programa social.")
    public List<Beneficiario> listarPorPrograma(@PathParam("idProgramaSocial") long idProgramaSocial) {
        return service.listarPorPrograma(idProgramaSocial);
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint de atualização de informações de beneficiário.")
    public Response atualizar(@PathParam("cpf") String cpf, @RequestBody AtualizarBeneficiarioDTO dto){
        Beneficiario beneficiario = service.atualizar(cpf, dto);
        return Response.ok().entity(beneficiario).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para apagar beneficiário existente.")
    public Response excluir(@PathParam("cpf") String cpf){
       service.excluir(cpf);
       return Response.noContent().build();
    }
}
