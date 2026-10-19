package br.com.raizdobem.api.integration;

import br.com.raizdobem.api.dto.response.PageResponse;
import br.com.raizdobem.api.service.AtendimentoService;
import br.com.raizdobem.api.service.BeneficiarioService;
import br.com.raizdobem.api.service.ColaboradorService;
import br.com.raizdobem.api.service.DentistaService;
import br.com.raizdobem.api.service.EnderecoService;
import br.com.raizdobem.api.service.PedidoAjudaService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Fase I-1: Validação de Autenticação e Autorização com JWT Real.
 * Complementa a Fase I-0 (que utilizou @TestSecurity) validando o processamento
 * criptográfico real do cabeçalho "Authorization: Bearer <token>", a verificação da assinatura
 * RSA 2048 (via par de chaves exclusivo de teste testKeyPublica.pem / testKeyPrivada.pem),
 * a validação do emissor (iss), a expiração temporal (exp) e o mapeamento de roles (groups).
 *
 * Todos os serviços de negócio permanecem mockados (@InjectMock) e o datasource
 * permanece isolado (Fail-Closed na porta 1 com pool zerado).
 */
@QuarkusTest
class SegurancaJwtRealTest {

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

    @Test
    @DisplayName("Cenário 1: Token válido, assinado corretamente e com claims aceitas permite acesso (HTTP 200)")
    void tokenValidoAdminDevePermitirAcessoRotaProtegida() {
        when(colaboradorService.listarComPaginacao(anyInt(), anyInt()))
                .thenReturn(new PageResponse<>(Collections.emptyList(), 0, 20, 0, 0));

        String token = Jwt.issuer("raiz-do-bem")
                .subject("admin@tdb.org")
                .claim("nome", "Admin Teste")
                .groups(Set.of("ADMIN"))
                .expiresIn(Duration.ofMinutes(30))
                .sign();

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/colaborador")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("Cenário 2: Ausência do cabeçalho Bearer em rota protegida deve retornar HTTP 401")
    void ausenciaDoCabecalhoBearerDeveRetornar401() {
        given()
        .when()
            .get("/colaborador")
        .then()
            .statusCode(401);

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @DisplayName("Cenário 3: Token Bearer malformado deve retornar HTTP 401")
    void tokenMalformadoDeveRetornar401() {
        given()
            .header("Authorization", "Bearer token-malformado-sem-estrutura-jwt")
        .when()
            .get("/colaborador")
        .then()
            .statusCode(401);

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @DisplayName("Cenário 4: Assinatura inválida (token assinado por chave privada não pareada) deve retornar HTTP 401")
    void tokenComAssinaturaInvalidaDeveRetornar401() throws Exception {
        // Gera par de chaves RSA avulso para simular um invasor com chave própria não confiável
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        PrivateKey chavePrivadaExterna = kpg.generateKeyPair().getPrivate();

        String tokenChaveFalsa = Jwt.issuer("raiz-do-bem")
                .subject("admin@tdb.org")
                .groups(Set.of("ADMIN"))
                .expiresIn(Duration.ofMinutes(30))
                .sign(chavePrivadaExterna);

        given()
            .header("Authorization", "Bearer " + tokenChaveFalsa)
        .when()
            .get("/colaborador")
        .then()
            .statusCode(401);

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @DisplayName("Cenário 5: Token expirado deve ser rejeitado pela verificação temporal (HTTP 401)")
    void tokenExpiradoDeveRetornar401() {
        // Expiração configurada 2 minutos no passado
        String tokenExpirado = Jwt.issuer("raiz-do-bem")
                .subject("admin@tdb.org")
                .groups(Set.of("ADMIN"))
                .expiresAt(Instant.now().minusSeconds(120).getEpochSecond())
                .sign();

        given()
            .header("Authorization", "Bearer " + tokenExpirado)
        .when()
            .get("/colaborador")
        .then()
            .statusCode(401);

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @DisplayName("Cenário 6: Emissor incorreto (divergente de 'raiz-do-bem') deve retornar HTTP 401")
    void emissorIncorretoDeveRetornar401() {
        String tokenEmissorInvalido = Jwt.issuer("emissor-invalido-nao-autorizado")
                .subject("admin@tdb.org")
                .groups(Set.of("ADMIN"))
                .expiresIn(Duration.ofMinutes(30))
                .sign();

        given()
            .header("Authorization", "Bearer " + tokenEmissorInvalido)
        .when()
            .get("/colaborador")
        .then()
            .statusCode(401);

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @DisplayName("Cenário 7: Token válido sem a role necessária deve ser barrado com HTTP 403 Forbidden")
    void tokenValidoSemRoleNecessariaDeveRetornar403() {
        String tokenColaborador = Jwt.issuer("raiz-do-bem")
                .subject("operador@tdb.org")
                .claim("nome", "Operador Teste")
                .groups(Set.of("COLABORADOR"))
                .expiresIn(Duration.ofMinutes(30))
                .sign();

        // DELETE /colaborador/{cpf} exige @RolesAllowed("ADMIN")
        given()
            .header("Authorization", "Bearer " + tokenColaborador)
        .when()
            .delete("/colaborador/12345678901")
        .then()
            .statusCode(403);

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @DisplayName("Cenário 8a: Token válido com papel ADMIN permite exclusão restrita (HTTP 204 No Content)")
    void tokenValidoComRoleAutorizadaDevePermitirOperacaoAdmin204() {
        when(colaboradorService.excluir(anyString())).thenReturn(1L);

        String tokenAdmin = Jwt.issuer("raiz-do-bem")
                .subject("admin@tdb.org")
                .claim("nome", "Admin Teste")
                .groups(Set.of("ADMIN"))
                .expiresIn(Duration.ofMinutes(30))
                .sign();

        given()
            .header("Authorization", "Bearer " + tokenAdmin)
        .when()
            .delete("/colaborador/12345678901")
        .then()
            .statusCode(204);
    }

    @Test
    @DisplayName("Cenário 8b: Token válido com papel COLABORADOR permite exportação CSV de dentistas (HTTP 200)")
    void tokenValidoColaboradorPodeExportarCsvDentistas200() {
        when(dentistaService.listarTodos()).thenReturn(Collections.emptyList());

        String tokenColaborador = Jwt.issuer("raiz-do-bem")
                .subject("operador@tdb.org")
                .claim("nome", "Operador Teste")
                .groups(Set.of("COLABORADOR"))
                .expiresIn(Duration.ofMinutes(30))
                .sign();

        given()
            .header("Authorization", "Bearer " + tokenColaborador)
        .when()
            .get("/dentista/exportarCsv")
        .then()
            .statusCode(200)
            .contentType("text/csv; charset=UTF-8");
    }
}
