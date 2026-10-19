package br.com.raizdobem.api.integration;

import br.com.raizdobem.api.service.AtendimentoService;
import br.com.raizdobem.api.service.BeneficiarioService;
import br.com.raizdobem.api.service.ColaboradorService;
import br.com.raizdobem.api.service.DentistaService;
import br.com.raizdobem.api.service.EnderecoService;
import br.com.raizdobem.api.service.PedidoAjudaService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * P1-03: Validação de Autorização RBAC (Role-Based Access Control) via @TestSecurity.
 * Verifica se a camada de autorização do Quarkus bloqueia perfis não autorizados (HTTP 403 Forbidden)
 * antes da execução de serviços de persistência e permite operações legítimas (HTTP 200/204)
 * com serviços mockados.
 *
 * NOTA: @TestSecurity simula papéis e identidades no Quarkus; não substitui o teste
 * de validação criptográfica de tokens JWT reais (escopo da Fase I-1).
 */
@QuarkusTest
class SegurancaAutorizacaoRbacP103Test {

    @InjectMock
    ColaboradorService colaboradorService;

    @InjectMock
    DentistaService dentistaService;

    @InjectMock
    BeneficiarioService beneficiarioService;

    @InjectMock
    AtendimentoService atendimentoService;

    @InjectMock
    PedidoAjudaService pedidoAjudaService;

    @InjectMock
    EnderecoService enderecoService;

    @ParameterizedTest(name = "DELETE {0} com papel COLABORADOR deve retornar HTTP 403")
    @TestSecurity(user = "operador@tdb.org", roles = "COLABORADOR")
    @ValueSource(strings = {
        "/colaborador/12345678901",
        "/dentista/12345678901",
        "/beneficiario/12345678901",
        "/atendimento/1",
        "/pedido-ajuda/1",
        "/endereco/1"
    })
    @DisplayName("P1-03: Perfil COLABORADOR não deve conseguir executar exclusões restritas a ADMIN (HTTP 403)")
    void colaboradorNaoDeveExecutarExclusoes(String rota) {
        given()
        .when()
            .delete(rota)
        .then()
            .statusCode(403);

        verifyNoInteractions(
            colaboradorService, dentistaService, beneficiarioService,
            atendimentoService, pedidoAjudaService, enderecoService
        );
    }

    @ParameterizedTest(name = "GET {0} com papel COLABORADOR deve retornar HTTP 403")
    @TestSecurity(user = "operador@tdb.org", roles = "COLABORADOR")
    @ValueSource(strings = {
        "/beneficiario/exportarCsv",
        "/atendimento/exportarCsv"
    })
    @DisplayName("P1-03: Perfil COLABORADOR não deve acessar exportações CSV exclusivas de ADMIN (HTTP 403)")
    void colaboradorNaoDeveExportarCsvRestrito(String rota) {
        given()
        .when()
            .get(rota)
        .then()
            .statusCode(403);

        verifyNoInteractions(beneficiarioService, atendimentoService);
    }

    @Test
    @TestSecurity(user = "operador@tdb.org", roles = "COLABORADOR")
    @DisplayName("P1-03: Perfil COLABORADOR não deve criar novo colaborador (HTTP 403)")
    void colaboradorNaoDeveCriarColaborador() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"cpf\":\"12345678901\",\"nomeCompleto\":\"Teste\",\"email\":\"teste@tdb.org\"}")
        .when()
            .post("/colaborador")
        .then()
            .statusCode(403);

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @TestSecurity(user = "operador@tdb.org", roles = "COLABORADOR")
    @DisplayName("P1-03: Perfil COLABORADOR não deve atualizar dados de colaborador (HTTP 403)")
    void colaboradorNaoDeveAtualizarColaborador() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"email\":\"novo@tdb.org\",\"senha\":\"senha123\"}")
        .when()
            .put("/colaborador/12345678901")
        .then()
            .statusCode(403);

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @TestSecurity(user = "operador@tdb.org", roles = "COLABORADOR")
    @DisplayName("P1-03: Perfil COLABORADOR TEM permissão para exportar CSV de dentistas (HTTP 200)")
    void colaboradorPodeExportarCsvDentistas() {
        when(dentistaService.listarTodos()).thenReturn(Collections.emptyList());

        given()
        .when()
            .get("/dentista/exportarCsv")
        .then()
            .statusCode(200)
            .contentType("text/csv; charset=UTF-8");
    }

    @Test
    @TestSecurity(user = "admin@tdb.org", roles = "ADMIN")
    @DisplayName("P1-03: Perfil ADMIN exclui colaborador com sucesso e recebe HTTP 204 No Content")
    void adminExcluiColaboradorComSucessoRetorna204() {
        when(colaboradorService.excluir(anyString())).thenReturn(1L);

        given()
        .when()
            .delete("/colaborador/12345678901")
        .then()
            .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin@tdb.org", roles = "ADMIN")
    @DisplayName("P1-03: Perfil ADMIN exclui dentista com sucesso e recebe HTTP 204 No Content")
    void adminExcluiDentistaComSucessoRetorna204() {
        when(dentistaService.excluir(anyString())).thenReturn(1L);

        given()
        .when()
            .delete("/dentista/12345678901")
        .then()
            .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin@tdb.org", roles = "ADMIN")
    @DisplayName("P1-03: Perfil ADMIN exclui atendimento com sucesso e recebe HTTP 204 No Content")
    void adminExcluiAtendimentoComSucessoRetorna204() {
        when(atendimentoService.excluir(anyLong())).thenReturn(true);

        given()
        .when()
            .delete("/atendimento/1")
        .then()
            .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin@tdb.org", roles = "ADMIN")
    @DisplayName("P1-03: Perfil ADMIN exclui beneficiário com sucesso e recebe HTTP 204 No Content")
    void adminExcluiBeneficiarioComSucessoRetorna204() {
        when(beneficiarioService.excluir(anyString())).thenReturn(true);

        given()
        .when()
            .delete("/beneficiario/12345678901")
        .then()
            .statusCode(204);
    }
}
