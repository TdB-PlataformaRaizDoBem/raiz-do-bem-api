package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.EnderecoCompletoRequest;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - Invalidação de Cache em EnderecoService")
class EnderecoCacheTest {

    @Test
    @DisplayName("Caminho Feliz - Deve verificar se o método de leitura listarTodos está anotado com @CacheResult(enderecos)")
    void deveVerificarAnotacaoCacheResultEmListarTodos() throws NoSuchMethodException {
        // Arrange & Act
        Method method = EnderecoService.class.getMethod("listarTodos");

        // Assert
        assertThat(method.isAnnotationPresent(CacheResult.class)).isTrue();
        CacheResult cacheResult = method.getAnnotation(CacheResult.class);
        assertThat(cacheResult.cacheName()).isEqualTo("enderecos");
    }

    @Test
    @DisplayName("Deve verificar se todos os métodos de escrita possuem @CacheInvalidateAll(enderecos)")
    void deveVerificarInvalidacaoDeCacheNosMetodosDeEscrita() throws NoSuchMethodException {
        // Arrange
        Method criar = EnderecoService.class.getMethod("criar", EnderecoCompletoRequest.class);
        Method atualizar = EnderecoService.class.getMethod("atualizarEndereco", Long.class, EnderecoCompletoRequest.class);
        Method excluir = EnderecoService.class.getMethod("excluir", Long.class);

        // Assert
        assertThat(criar.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(criar.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("enderecos");

        assertThat(atualizar.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(atualizar.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("enderecos");

        assertThat(excluir.isAnnotationPresent(CacheInvalidateAll.class)).isTrue();
        assertThat(excluir.getAnnotation(CacheInvalidateAll.class).cacheName()).isEqualTo("enderecos");
    }
}
