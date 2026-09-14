package br.com.raizdobem.api.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - CpfValidatorUtil")
class CpfValidatorUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",
            "529.982.247-25",
            "98765432100",
            "987.654.321-00",
            "12345678909",
            "123.456.789-09"
    })
    @DisplayName("Deve retornar true para CPFs válidos com ou sem pontuação")
    void deveRetornarTrueParaCpfsValidos(String cpf) {
        // Act
        boolean valido = CpfValidatorUtil.cpfValido(cpf);

        // Assert
        assertThat(valido).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Deve retornar false para CPFs nulos, vazios ou apenas espaços")
    void deveRetornarFalseParaCpfsNulosOuVazios(String cpf) {
        // Act
        boolean valido = CpfValidatorUtil.cpfValido(cpf);

        // Assert
        assertThat(valido).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "1234567890",       // 10 dígitos
            "123456789012",     // 12 dígitos
            "abc12345678",      // letras
            "!@#$$%¨&*()",      // caracteres especiais sem dígitos suficientes
            "123.456.78"        // incompleto
    })
    @DisplayName("Deve retornar false para CPFs com comprimento diferente de 11 dígitos")
    void deveRetornarFalseParaCpfsComTamanhoInvalido(String cpf) {
        // Act
        boolean valido = CpfValidatorUtil.cpfValido(cpf);

        // Assert
        assertThat(valido).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000",
            "11111111111",
            "22222222222",
            "33333333333",
            "44444444444",
            "55555555555",
            "66666666666",
            "77777777777",
            "88888888888",
            "99999999999"
    })
    @DisplayName("Deve retornar false para CPFs com todos os dígitos repetidos")
    void deveRetornarFalseParaCpfsComDigitosRepetidos(String cpf) {
        // Act
        boolean valido = CpfValidatorUtil.cpfValido(cpf);

        // Assert
        assertThat(valido).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224720", // Primeiro DV incorreto
            "52998224705", // Segundo DV incorreto
            "12345678901", // DV incorreto (o correto é 09)
            "98765432101", // DV incorreto (o correto é 00)
            "11122233344"  // DVs incorretos
    })
    @DisplayName("Deve retornar false para CPFs com dígitos verificadores inválidos")
    void deveRetornarFalseParaCpfsComDigitosVerificadoresInvalidos(String cpf) {
        // Act
        boolean valido = CpfValidatorUtil.cpfValido(cpf);

        // Assert
        assertThat(valido).isFalse();
    }

    @Test
    @DisplayName("Deve validar a existência do construtor privado para classe utilitária")
    void deveCobrirConstrutorPrivado() throws Exception {
        Constructor<CpfValidatorUtil> constructor = CpfValidatorUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        CpfValidatorUtil instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}
