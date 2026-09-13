package br.com.raizdobem.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - ValidacaoService")
class ValidacaoServiceTest {

    @Test
    @DisplayName("Deve validar com sucesso quando o CPF contiver exatamente 11 dígitos numéricos")
    void deveValidarCpfQuandoFormatoForValidoComOnzeDigitos() {
        // Arrange
        String cpfValido = "12345678901";

        // Act
        boolean resultado = ValidacaoService.validarCpf(cpfValido);

        // Assert
        assertThat(resultado).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",
            "   ",
            "1234567890",       // 10 dígitos (incompleto)
            "123456789012",     // 12 dígitos (excesso)
            "1234567890A",     // Contém caractere alfabético
            "123.456.789-01",  // Contém pontuação
            "abcdefghijk"      // Totalmente alfabético
    })
    @DisplayName("Deve invalidar CPF quando for nulo, vazio, tiver tamanho diferente de 11 ou caracteres não numéricos")
    void deveInvalidarCpfQuandoFormatoForInvalido(String cpfInvalido) {
        // Arrange & Act
        boolean resultado = ValidacaoService.validarCpf(cpfInvalido);

        // Assert
        assertThat(resultado).isFalse();
    }
}
