package br.com.raizdobem.api.integration;

import br.com.raizdobem.api.service.AtendimentoService;
import br.com.raizdobem.api.service.BeneficiarioService;
import br.com.raizdobem.api.service.ColaboradorService;
import br.com.raizdobem.api.service.DentistaService;
import br.com.raizdobem.api.service.EnderecoService;
import br.com.raizdobem.api.service.PedidoAjudaService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * P1-01: Validação de Autenticação em Camada HTTP (Fail-Closed).
 * Verifica se endpoints protegidos rejeitam requisições anônimas com HTTP 401 Unauthorized
 * antes que qualquer lógica de negócio ou persistência seja alcançada.
 *
 * NOTA: Endpoints públicos (@PermitAll como POST /dentista, POST /pedido-ajuda,
 * /especialidades/*, /programas-sociais/* e /auth/*) NÃO pertencem a este teste.
 */
@QuarkusTest
class SegurancaAutenticacaoP101Test {

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

    @ParameterizedTest(name = "GET {0} anônimo deve retornar HTTP 401")
    @ValueSource(strings = {
        "/colaborador",
        "/dentista",
        "/dentista/exportarCsv",
        "/beneficiario",
        "/beneficiario/exportarCsv",
        "/atendimento/",
        "/atendimento/exportarCsv",
        "/pedido-ajuda",
        "/endereco"
    })
    @DisplayName("P1-01: Endpoints protegidos por GET devem retornar HTTP 401 para requisições anônimas")
    void getProtegidoSemTokenRetorna401(String rota) {
        given()
        .when()
            .get(rota)
        .then()
            .statusCode(401);

        verifyNoInteractions(
            colaboradorService, dentistaService, beneficiarioService,
            atendimentoService, pedidoAjudaService, enderecoService
        );
    }

    @ParameterizedTest(name = "DELETE {0} anônimo deve retornar HTTP 401")
    @ValueSource(strings = {
        "/colaborador/12345678901",
        "/dentista/12345678901",
        "/beneficiario/12345678901",
        "/atendimento/1",
        "/pedido-ajuda/1",
        "/endereco/1"
    })
    @DisplayName("P1-01: Endpoints de exclusão devem retornar HTTP 401 para requisições anônimas")
    void deleteProtegidoSemTokenRetorna401(String rota) {
        given()
        .when()
            .delete(rota)
        .then()
            .statusCode(401);

        verifyNoInteractions(
            colaboradorService, dentistaService, beneficiarioService,
            atendimentoService, pedidoAjudaService, enderecoService
        );
    }
}
