package br.com.raizdobem.api.util;

import br.com.raizdobem.api.dto.response.AtendimentoResponse;
import br.com.raizdobem.api.dto.response.BeneficiarioResponse;
import br.com.raizdobem.api.dto.response.DentistaResponse;
import br.com.raizdobem.api.dto.response.EnderecoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - CsvUtil")
class CsvUtilTest {

    @Test
    @DisplayName("Deve gerar CSV de atendimentos com cabeçalho delimitado por pipe")
    void deveGerarCsvAtendimentos() {
        // Arrange
        AtendimentoResponse a = new AtendimentoResponse(
                1L,
                "PRONT-100",
                "Beneficiário A",
                "Dentista B",
                "11999990000",
                "dentista@email.com",
                "Rua X",
                LocalDate.of(2026, 3, 1),
                "2026-03-10"
        );

        // Act
        String csv = CsvUtil.gerarCsvAtendimentos(List.of(a));

        // Assert
        assertThat(csv).isNotNull();
        assertThat(csv).contains("ID|Prontuário|Beneficiário|Dentista|Data Inicial|Data Final");
        assertThat(csv).contains("1|PRONT-100|Beneficiário A|Dentista B|2026-03-01|2026-03-10");
    }

    @Test
    @DisplayName("Deve gerar CSV de dentistas com formato correto")
    void deveGerarCsvDentistas() {
        // Arrange
        DentistaResponse d = new DentistaResponse(
                1L,
                "12345",
                "12345678901",
                "Dr. Roberto",
                "M",
                "roberto@odonto.com",
                "11999991111",
                "VOLUNTARIO",
                List.of("Ortodontia"),
                List.of("Dentista do Bem"),
                "true",
                "Rua Boa Vista",
                "100",
                "São Paulo",
                "SP",
                "01001000"
        );

        // Act
        String csv = CsvUtil.gerarCsvDentistas(List.of(d));

        // Assert
        assertThat(csv).isNotNull();
        assertThat(csv).contains("ID,CRO,CPF,Nome Completo,Sexo,Email,Telefone,Categoria,Disponível");
        assertThat(csv).contains("Dr. Roberto");
        assertThat(csv).contains("Ortodontia");
    }

    @Test
    @DisplayName("Deve gerar CSV de beneficiários com cabeçalho delimitado por vírgula")
    void deveGerarCsvBeneficiarios() {
        // Arrange
        EnderecoResponse end = new EnderecoResponse(
                1L,
                "Praça da Sé",
                "01001000",
                "100",
                "Sé",
                "São Paulo",
                "SP",
                "RESIDENCIAL"
        );
        BeneficiarioResponse b = new BeneficiarioResponse(
                1L,
                "12345678901",
                "Maria Joana",
                LocalDate.of(2012, 5, 10),
                "11988887777",
                "maria@email.com",
                null,
                "Programa 1",
                end
        );

        // Act
        String csv = CsvUtil.gerarCsvBeneficiarios(List.of(b));

        // Assert
        assertThat(csv).isNotNull();
        assertThat(csv).contains("ID,CPF,Nome Completo,Data de Nascimento,Telefone,Email,IdPedidoAjuda,ProgramaSocial,Logradouro,Número,Cidade,Estado");
        assertThat(csv).contains("1,12345678901,Maria Joana,2012-05-10,11988887777,maria@email.com,,Programa 1,Praça da Sé,100,São Paulo,SP");
    }

    @Test
    @DisplayName("Deve gerar CSV de beneficiários sem NullPointerException quando pedido e endereço forem nulos")
    void deveGerarCsvBeneficiariosComPedidoEEnderecoNulos() {
        // Arrange
        BeneficiarioResponse b = new BeneficiarioResponse(
                2L,
                "98765432100",
                "Carlos Silva",
                LocalDate.of(2015, 8, 20),
                "11977776666",
                "carlos@email.com",
                null,
                "Apolônias do Bem",
                null
        );

        // Act
        String csv = CsvUtil.gerarCsvBeneficiarios(List.of(b));

        // Assert
        assertThat(csv).isNotNull();
        assertThat(csv).contains("2,98765432100,Carlos Silva,2015-08-20,11977776666,carlos@email.com,,Apolônias do Bem,,,,");
    }

    @Test
    @DisplayName("Deve gerar nome do arquivo com formato tipo_dd-MM-yyyy.csv")
    void deveGerarNomeArquivoComDataAtual() {
        // Arrange
        String tipo = "RelatorioAtendimentos";
        String hojeFormatado = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Act
        String nomeArquivo = CsvUtil.gerarNomeArquivo(tipo);

        // Assert
        assertThat(nomeArquivo).isEqualTo(tipo + "_" + hojeFormatado + ".csv");
    }
}
