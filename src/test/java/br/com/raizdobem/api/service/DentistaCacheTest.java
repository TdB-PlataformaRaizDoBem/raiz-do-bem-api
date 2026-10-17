package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.DentistaCreateRequest;
import br.com.raizdobem.api.dto.request.DentistaUpdateRequest;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - Invalidação de Cache em DentistaService")
class DentistaCacheTest {

    @Test
    @DisplayName("Caminho Feliz - Deve verificar se o método de leitura paginado está anotado com @CacheResult(dentistas)")
    void deveVerificarAnotacaoCacheResultEmListarComPaginacao() throws NoSuchMethodException {
        // Arrange & Act
        Method method = DentistaService.class.getMethod("listarComPaginacao", int.class, int.class);

        // Assert
        assertThat(method.isAnnotationPresent(CacheResult.class)).isTrue();
        CacheResult cacheResult = method.getAnnotation(CacheResult.class);
        assertThat(cacheResult.cacheName()).isEqualTo("dentistas");
    }

    @Test
    @DisplayName("Deve verificar se todos os métodos de escrita possuem @CacheInvalidateAll(dentistas)")
    void deveVerificarInvalidacaoDeCacheNosMetodosDeEscrita() throws NoSuchMethodException {
        // Arrange
        Method criar = DentistaService.class.getMethod("criarDentista", DentistaCreateRequest.class);
        Method atualizar = DentistaService.class.getMethod("atualizar", String.class, DentistaUpdateRequest.class);
        Method excluir = DentistaService.class.getMethod("excluir", String.class);

        // Assert
        assertThat(criar.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(criar.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("dentistas");

        assertThat(atualizar.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(atualizar.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("dentistas");

        assertThat(excluir.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(excluir.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("dentistas");
    }
}
