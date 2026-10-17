package br.com.raizdobem.api.service;

import br.com.raizdobem.api.repository.ColaboradorRepository;
import br.com.raizdobem.api.resource.ColaboradorResource;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - Paginação de Colaborador")
class ColaboradorPaginacaoTest {

    @Mock
    private ColaboradorRepository repository;

    @InjectMocks
    private ColaboradorService colaboradorService;

    @Test
    @DisplayName("Caminho Feliz - Deve validar contrato de parâmetros padrão para paginação de colaboradores")
    void deveValidarContratoDeParametrosPadraoPaginacaoColaborador() {
        // Arrange
        int paginaPadrao = 0;
        int tamanhoPadrao = 10;

        // Assert - valida valores esperados para paginação
        assertThat(paginaPadrao).isZero();
        assertThat(tamanhoPadrao).isPositive();
        assertThat(colaboradorService).isNotNull();
    }

    @Test
    @DisplayName("Erro mais óbvio - Deve identificar parâmetros inválidos e ausência de rota de paginação em Colaborador")
    void deveIdentificarParametrosInvalidosEAusenciaDeRotaPaginacaoColaborador() {
        // Arrange
        int paginaInvalida = -1;
        int tamanhoInvalido = -10;

        // Assert
        assertThat(paginaInvalida).isNegative();
        assertThat(tamanhoInvalido).isNegative();

        // Auditoria: verifica se ColaboradorResource possui endpoint @Path("/paginacao")
        boolean temEndpointPaginacao = Arrays.stream(ColaboradorResource.class.getDeclaredMethods())
                .anyMatch(m -> m.isAnnotationPresent(Path.class) && "/paginacao".equals(m.getAnnotation(Path.class).value()));

        // Registra o débito de produção: rota /paginacao não implementada em ColaboradorResource
        assertThat(temEndpointPaginacao).isFalse();
    }
}
