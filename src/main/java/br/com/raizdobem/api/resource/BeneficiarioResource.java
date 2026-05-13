package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtualizarBeneficiarioDTO;
import br.com.raizdobem.api.dto.request.CriarBeneficiarioDTO;
import br.com.raizdobem.api.dto.response.AtendimentoDTO;
import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.service.BeneficiarioService;
import br.com.raizdobem.api.util.CsvUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
    public Response listarTodos(){
        List<BeneficiarioDTO> beneficiarios = service.listarTodos();
        return Response.ok(beneficiarios).build();
    }

    @POST
    @Operation(summary = "Endpoint para a criação de beneficiários.")
    public Response criar(@Valid CriarBeneficiarioDTO request){
        BeneficiarioDTO beneficiario = service.criarBeneficiario(request);
        return Response.status(Response.Status.CREATED).entity(beneficiario).build();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para encontrar um beneficiário específico.")
    public Response buscarPorCpf(@PathParam("cpf") String cpf){
        BeneficiarioDTO beneficiario = service.buscarPorCpf(cpf);
        return Response.ok(beneficiario).build();
    }

    @GET
    @Path("/cidade/{cidade}")
    @Operation(summary = "Endpoint para listar beneficiários por cidade.")
    public Response listarPorCidade(@PathParam("cidade") String cidade) {
        List<BeneficiarioDTO> beneficiarios = service.listarPorCidade(cidade);
        return Response.ok(beneficiarios).build();
    }

    @GET
    @Path("/programa/{idProgramaSocial}")
    @Operation(summary = "Endpoint para listar beneficiários por programa social.")
    public Response listarPorPrograma(@PathParam("idProgramaSocial") long idProgramaSocial) {
        List<BeneficiarioDTO> beneficiarios = service.listarPorPrograma(idProgramaSocial);
        return Response.ok(beneficiarios).build();
    }

    @GET
    @Path("/exportarCsv")
    @Produces("text/csv")
    @Operation(summary = "Endpoint para a exportar todos os atendimentos em um arquivo csv.")
    public Response exportarCsv(){
        List<BeneficiarioDTO> listaBeneficiarios = service.listarParaExportacao();

        String csv = CsvUtil.gerarCsvBeneficiarios(listaBeneficiarios);

        return Response.ok(csv).header("Content-Disposition",
                        "attachment; filename=beneficiarios.csv")
                .build();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint de atualização de informações de beneficiário.")
    public Response atualizar(@PathParam("cpf") String cpf, @Valid @RequestBody AtualizarBeneficiarioDTO dto){
        BeneficiarioDTO beneficiario = service.atualizar(cpf, dto);
        return Response.ok().entity(beneficiario).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para apagar beneficiário existente.")
    public Response excluir(@PathParam("cpf") String cpf){
       boolean exclusao = service.excluir(cpf);
       if(!exclusao)
           throw new RequisicaoInvalidaException("Não foi possível excluir beneficiário.");
       return Response.noContent().build();
    }
}
