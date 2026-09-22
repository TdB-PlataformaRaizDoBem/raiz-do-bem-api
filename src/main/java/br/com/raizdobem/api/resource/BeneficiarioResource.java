package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.BeneficiarioUpdateRequest;
import br.com.raizdobem.api.dto.request.BeneficiarioCreateRequest;
import br.com.raizdobem.api.dto.response.BeneficiarioResponse;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.service.BeneficiarioService;
import br.com.raizdobem.api.util.CsvUtil;
import io.vertx.core.cli.annotations.Hidden;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.nio.charset.StandardCharsets;
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
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response listarTodos(){
        return Response.ok(service.listarTodos()).build();
    }

    @GET
    @Path("/paginacao")
    @Operation(summary = "Endpoint de listagem dos beneficiários cadastrados.")
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response listarTodosComPaginacao(
            @QueryParam("pagina") @DefaultValue("0") int pagina,
            @QueryParam("size") @DefaultValue("20") int tamanho){
        return Response.ok(service.listarTodosPaginacao(pagina, tamanho)).build();
    }



    @POST
    @Operation(summary = "Endpoint para a criação de beneficiário, de um pedido de ajuda aprovado.")
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response criar(@Valid BeneficiarioCreateRequest request){
        BeneficiarioResponse beneficiario = service.criarBeneficiario(request);
        return Response.status(Response.Status.CREATED).entity(beneficiario).build();
    }

    @GET
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para encontrar um beneficiário específico.")
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response buscarPorCpf(@PathParam("cpf") String cpf){
        BeneficiarioResponse beneficiario = service.buscarPorCpf(cpf);
        return Response.ok(beneficiario).build();
    }

    @GET
    @Path("/cidade/{cidade}")
    @Operation(summary = "Endpoint para listar beneficiários por cidade.")
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response listarPorCidade(@PathParam("cidade") String cidade) {
        List<BeneficiarioResponse> beneficiarios = service.listarPorCidade(cidade);
        return Response.ok(beneficiarios).build();
    }

    @GET
    @Path("/programa/{idProgramaSocial}")
    @Operation(summary = "Endpoint para listar beneficiários por programa social.")
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response listarPorPrograma(@PathParam("idProgramaSocial") long idProgramaSocial) {
        List<BeneficiarioResponse> beneficiarios = service.listarPorPrograma(idProgramaSocial);
        return Response.ok(beneficiarios).build();
    }

    @GET
    @Path("/exportarCsv")
    @Produces("text/csv; charset=UTF-8")
    @Operation(summary = "Endpoint para exportar todos os atendimentos em um arquivo csv.")
    @RolesAllowed("ADMIN")
    public Response exportarCsv(){
        String csv = CsvUtil.gerarCsvBeneficiarios(service.listarTodos());

        String nomeArquivo = CsvUtil.gerarNomeArquivo("Beneficiarios");

        byte [] csvBytes = csv.getBytes(StandardCharsets.UTF_8);

        return Response.ok(csvBytes).header("Content-Disposition",
                        "attachment; filename=\"" + nomeArquivo + "\"")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .build();
    }

    @PUT
    @Path("/{cpf}")
    @Operation(summary = "Endpoint de atualização de informações de beneficiário.")
    @RolesAllowed({"ADMIN", "COLABORADOR"})
    public Response atualizar(@PathParam("cpf") String cpf, @Valid @RequestBody BeneficiarioUpdateRequest dto){
        BeneficiarioResponse beneficiario = service.atualizar(cpf, dto);
        return Response.ok().entity(beneficiario).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Operation(summary = "Endpoint para apagar beneficiário existente.")
    @RolesAllowed("ADMIN")
    @Hidden
    public Response excluir(@PathParam("cpf") String cpf){
       boolean exclusao = service.excluir(cpf);
       if(!exclusao)
           throw new RequisicaoInvalidaException("Não foi possível excluir beneficiário.");
       return Response.noContent().build();
    }
}
