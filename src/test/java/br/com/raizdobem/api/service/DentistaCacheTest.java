package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.DentistaCreateRequest;
import br.com.raizdobem.api.dto.request.DentistaUpdateRequest;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - Invalidação de Cache em DentistaService")
class DentistaCacheTest {

    @Test
    @DisplayName("Caminho Feliz - Deve verificar se o método de leitura listarTodos está anotado com @CacheResult(dentistas)")
    void deveVerificarAnotacaoCacheResultEmListarTodos() throws NoSuchMethodException {
        // Arrange & Act
        Method method = DentistaService.class.getMethod("listarTodos");

        // Assert
        assertThat(method.isAnnotationPresent(CacheResult.class)).isTrue();
        CacheResult cacheResult = method.getAnnotation(CacheResult.class);
        assertThat(cacheResult.cacheName()).isEqualTo("dentistas");
    }

    @Test
    @DisplayName("Erro mais óbvio / Auditoria - Deve auditar ausência de anotações de invalidação de cache nos métodos de escrita")
    void deveAuditarAusenciaDeInvalidacaoDeCacheNosMetodosDeEscrita() throws NoSuchMethodException {
        // Arrange
        Method criar = DentistaService.class.getMethod("criarDentista", DentistaCreateRequest.class);
        Method atualizar = DentistaService.class.getMethod("atualizar", String.class, DentistaUpdateRequest.class);
        Method excluir = DentistaService.class.getMethod("excluir", String.class);

        // Act - verifica se os métodos possuem @CacheInvalidateAll ou @CacheInvalidate
        boolean criarPossuiInvalidacao = criar.isAnnotationPresent(CacheInvalidateAll.class)
                || criar.isAnnotationPresent(CacheInvalidate.class);
        boolean atualizarPossuiInvalidacao = atualizar.isAnnotationPresent(CacheInvalidateAll.class)
                || atualizar.isAnnotationPresent(CacheInvalidate.class);
        boolean excluirPossuiInvalidacao = excluir.isAnnotationPresent(CacheInvalidateAll.class)
                || excluir.isAnnotationPresent(CacheInvalidate.class);

        // Assert - registra o débito técnico: métodos de escrita em DentistaService não possuem @CacheInvalidateAll(cacheName = "dentistas")
        assertThat(criarPossuiInvalidacao).isFalse();
        assertThat(atualizarPossuiInvalidacao).isFalse();
        assertThat(excluirPossuiInvalidacao).isFalse();
    }
}
