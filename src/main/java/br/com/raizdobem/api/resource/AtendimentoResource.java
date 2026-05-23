package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.dto.request.AtualizarAtendimentoDTO;
import br.com.raizdobem.api.dto.request.CriarAtendimentoDTO;
import br.com.raizdobem.api.dto.response.AtendimentoDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.service.AtendimentoService;
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
@Path("/atendimento")
@Tag(name = "Atendimento", description = "Disponibiliza funcionalidades relacionadas aos atendimentos.")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AtendimentoResource {
    @Inject
    AtendimentoService service;
    
    @GET
    @Operation(summary = "Endpoint de listagem dos atendimentos.")
    public Response listarTodos(){
        List<AtendimentoDTO> pedidos = service.listarAtendimentos();
        if(pedidos == null || pedidos.isEmpty()){
            throw new NaoEncontradoException("Nenhum atendimento encontrado.");
        }
        return Response.ok(pedidos).build();
    }

    @POST
    @Operation(summary = "Endpoint de criação dos atendimentos de beneficiários cadastrados.")
    public Response criar(@Valid CriarAtendimentoDTO request) {
        AtendimentoDTO atendimento = service.criarAtendimento(request);
        if (atendimento == null) {
            throw new RequisicaoInvalidaException("Não foi possível criar atendimento.");
        }
        return Response.ok(atendimento).build();
    }

    @GET
    @Path("/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf){
        AtendimentoDTO atendimento = service.buscarPorCpf(cpf);
        if (atendimento == null) {
            throw new NaoEncontradoException("Não foi possível encontrar atendimento com o CPF inserido.");
        }
        return Response.ok(atendimento).build();
    }

    @GET
    @Path("/exportarCsv")
    @Produces("text/csv")
    @Operation(summary = "Endpoint para exportar todos os atendimentos em um arquivo csv.")
    public Response exportarCsv(){
        List<AtendimentoDTO> lista = service.listarParaExportacao();

        String csv = CsvUtil.gerarCsvAtendimentos(lista);

        return Response.ok(csv).header("Content-Disposition",
                        "attachment; filename=atendimentos.csv")
                .build();
    }

    @PUT
    @Operation(summary = "Endpoint para finalizar atendimento.")
    @Path("/{cpf}")
    public Response atualizar(@PathParam("cpf") String cpf, @RequestBody AtualizarAtendimentoDTO dto){
        service.encerrarAtendimento(cpf, dto);
        return Response.ok().build();
    }

    @DELETE
    @Operation(summary = "Endpoint para exclusão de atendimento.")
    @Path("/{id}")
    public Response excluirAtendimento(@PathParam("id") Long id){
        boolean apagado = service.excluir(id);

        if(apagado){
            return Response.noContent().build();
        }
        throw new NaoEncontradoException("Pedido de ajuda não encontrado para exclusão.");
    }
}
