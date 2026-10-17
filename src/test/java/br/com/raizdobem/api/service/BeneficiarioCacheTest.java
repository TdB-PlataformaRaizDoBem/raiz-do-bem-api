package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.BeneficiarioCreateRequest;
import br.com.raizdobem.api.dto.request.BeneficiarioUpdateRequest;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - Invalidação de Cache em BeneficiarioService")
class BeneficiarioCacheTest {

    @Test
    @DisplayName("Caminho Feliz - Deve verificar se o método paginado está anotado com @CacheResult(beneficiarios)")
    void deveVerificarAnotacaoCacheResultNosMetodosDeLeitura() throws NoSuchMethodException {
        // Arrange & Act
        Method listarTodosPaginacao = BeneficiarioService.class.getMethod("listarTodosPaginacao", int.class, int.class);

        // Assert
        assertThat(listarTodosPaginacao.isAnnotationPresent(CacheResult.class)).isTrue();
        assertThat(listarTodosPaginacao.getAnnotation(CacheResult.class).cacheName()).isEqualTo("beneficiarios");
    }

    @Test
    @DisplayName("Deve verificar se todos os métodos de escrita possuem @CacheInvalidateAll(beneficiarios)")
    void deveVerificarInvalidacaoDeCacheNosMetodosDeEscrita() throws NoSuchMethodException {
        // Arrange
        Method criar = BeneficiarioService.class.getMethod("criarBeneficiario", BeneficiarioCreateRequest.class);
        Method atualizar = BeneficiarioService.class.getMethod("atualizar", String.class, BeneficiarioUpdateRequest.class);
        Method excluir = BeneficiarioService.class.getMethod("excluir", String.class);

        // Assert
        assertThat(criar.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(criar.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("beneficiarios");

        assertThat(atualizar.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(atualizar.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("beneficiarios");

        assertThat(excluir.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(excluir.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("beneficiarios");
    }
}
