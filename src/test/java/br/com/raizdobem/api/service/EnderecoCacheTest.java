package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.EnderecoCompletoRequest;
import io.quarkus.cache.CacheInvalidate;
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
    @DisplayName("Erro mais óbvio / Auditoria - Deve auditar ausência de anotações de invalidação de cache nos métodos de escrita")
    void deveAuditarAusenciaDeInvalidacaoDeCacheNosMetodosDeEscrita() throws NoSuchMethodException {
        // Arrange
        Method criar = EnderecoService.class.getMethod("criar", EnderecoCompletoRequest.class);
        Method atualizar = EnderecoService.class.getMethod("atualizarEndereco", Long.class, EnderecoCompletoRequest.class);
        Method excluir = EnderecoService.class.getMethod("excluir", Long.class);

        // Act
        boolean criarPossuiInvalidacao = criar.isAnnotationPresent(CacheInvalidateAll.class)
                || criar.isAnnotationPresent(CacheInvalidate.class);
        boolean atualizarPossuiInvalidacao = atualizar.isAnnotationPresent(CacheInvalidateAll.class)
                || atualizar.isAnnotationPresent(CacheInvalidate.class);
        boolean excluirPossuiInvalidacao = excluir.isAnnotationPresent(CacheInvalidateAll.class)
                || excluir.isAnnotationPresent(CacheInvalidate.class);

        // Assert - registra o débito técnico: métodos de escrita em EnderecoService não possuem @CacheInvalidateAll(cacheName = "enderecos")
        assertThat(criarPossuiInvalidacao).isFalse();
        assertThat(atualizarPossuiInvalidacao).isFalse();
        assertThat(excluirPossuiInvalidacao).isFalse();
    }
}
