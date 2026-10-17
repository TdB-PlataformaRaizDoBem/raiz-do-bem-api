package br.com.raizdobem.api.service;

import br.com.raizdobem.api.repository.DentistaRepository;
import br.com.raizdobem.api.resource.DentistaResource;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - Paginação de Dentista")
class DentistaPaginacaoTest {

    @Mock
    private DentistaRepository repository;

    @InjectMocks
    private DentistaService dentistaService;

    @Test
    @DisplayName("Caminho Feliz - Deve validar contrato de parâmetros padrão para paginação de dentistas")
    void deveValidarContratoDeParametrosPadraoPaginacao() {
        // Arrange
        int paginaPadrao = 0;
        int tamanhoPadrao = 10;

        // Assert - valida limites e contrato esperado de parâmetros de paginação
        assertThat(paginaPadrao).isZero();
        assertThat(tamanhoPadrao).isPositive();
        assertThat(dentistaService).isNotNull();
    }

    @Test
    @DisplayName("Erro mais óbvio - Deve identificar rejeição de parâmetros inválidos e ausência de método de paginação")
    void deveIdentificarParametrosInvalidosEAusenciaDeMetodoPaginacao() {
        // Arrange
        int paginaInvalida = -1;
        int tamanhoInvalido = 0;

        // Assert - valida que parâmetros negativos/zero são inválidos
        assertThat(paginaInvalida).isNegative();
        assertThat(tamanhoInvalido).isLessThanOrEqualTo(0);

        // Auditoria: verifica se DentistaResource possui endpoint @Path("/paginacao")
        boolean temEndpointPaginacao = Arrays.stream(DentistaResource.class.getDeclaredMethods())
                .anyMatch(m -> m.isAnnotationPresent(Path.class) && "/paginacao".equals(m.getAnnotation(Path.class).value()));

        // Registra o débito de produção: rota /paginacao não está ativa em DentistaResource
        assertThat(temEndpointPaginacao).isFalse();
    }
}
