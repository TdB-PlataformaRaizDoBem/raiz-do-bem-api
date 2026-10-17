package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.BeneficiarioCreateRequest;
import br.com.raizdobem.api.dto.request.BeneficiarioUpdateRequest;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - Invalidação de Cache em BeneficiarioService")
class BeneficiarioCacheTest {

    @Test
    @DisplayName("Caminho Feliz - Deve verificar se os métodos de leitura estão anotados com @CacheResult(beneficiarios)")
    void deveVerificarAnotacaoCacheResultNosMetodosDeLeitura() throws NoSuchMethodException {
        // Arrange & Act
        Method listarTodos = BeneficiarioService.class.getMethod("listarTodos");
        Method listarTodosPaginacao = BeneficiarioService.class.getMethod("listarTodosPaginacao", int.class, int.class);

        // Assert
        assertThat(listarTodos.isAnnotationPresent(CacheResult.class)).isTrue();
        assertThat(listarTodos.getAnnotation(CacheResult.class).cacheName()).isEqualTo("beneficiarios");

        assertThat(listarTodosPaginacao.isAnnotationPresent(CacheResult.class)).isTrue();
        assertThat(listarTodosPaginacao.getAnnotation(CacheResult.class).cacheName()).isEqualTo("beneficiarios");
    }

    @Test
    @DisplayName("Erro mais óbvio / Auditoria - Deve auditar ausência de anotações de invalidação de cache nos métodos de escrita")
    void deveAuditarAusenciaDeInvalidacaoDeCacheNosMetodosDeEscrita() throws NoSuchMethodException {
        // Arrange
        Method criar = BeneficiarioService.class.getMethod("criarBeneficiario", BeneficiarioCreateRequest.class);
        Method atualizar = BeneficiarioService.class.getMethod("atualizar", String.class, BeneficiarioUpdateRequest.class);
        Method excluir = BeneficiarioService.class.getMethod("excluir", String.class);

        // Act
        boolean criarPossuiInvalidacao = criar.isAnnotationPresent(CacheInvalidateAll.class)
                || criar.isAnnotationPresent(CacheInvalidate.class);
        boolean atualizarPossuiInvalidacao = atualizar.isAnnotationPresent(CacheInvalidateAll.class)
                || atualizar.isAnnotationPresent(CacheInvalidate.class);
        boolean excluirPossuiInvalidacao = excluir.isAnnotationPresent(CacheInvalidateAll.class)
                || excluir.isAnnotationPresent(CacheInvalidate.class);

        // Assert - registra o débito técnico: métodos de escrita em BeneficiarioService não possuem @CacheInvalidateAll(cacheName = "beneficiarios")
        assertThat(criarPossuiInvalidacao).isFalse();
        assertThat(atualizarPossuiInvalidacao).isFalse();
        assertThat(excluirPossuiInvalidacao).isFalse();
    }
}
