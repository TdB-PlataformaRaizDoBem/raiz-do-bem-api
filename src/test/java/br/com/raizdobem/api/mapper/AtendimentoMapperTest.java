package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.request.AtendimentoCreateRequest;
import br.com.raizdobem.api.dto.response.AtendimentoResponse;
import br.com.raizdobem.api.entity.Atendimento;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - AtendimentoMapper")
class AtendimentoMapperTest {

    private Atendimento criarAtendimento(Long id, boolean comDentistaEEndereco, boolean finalizado) {
        Atendimento atendimento = new Atendimento();
        atendimento.setId(id);
        atendimento.setProntuario("PRONT-001");
        atendimento.setDataInicial(LocalDate.of(2026, 1, 10));

        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setId(1L);
        beneficiario.setNomeCompleto("Carlos Silva");
        beneficiario.setCpf("12345678901");
        atendimento.setBeneficiario(beneficiario);

        if (comDentistaEEndereco) {
            Dentista dentista = new Dentista();
            dentista.setId(2L);
            dentista.setNomeCompleto("Dra. Beatriz Santos");
            dentista.setTelefone("11988887777");
            dentista.setEmail("beatriz@odonto.com");

            Endereco endereco = new Endereco();
            endereco.setLogradouro("Rua das Flores");
            endereco.setNumero("123");
            endereco.setCidade("São Paulo");
            endereco.setEstado("SP");
            dentista.setEndereco(endereco);

            atendimento.setDentista(dentista);
        }

        if (finalizado) {
            atendimento.setDataFinal(LocalDate.of(2026, 2, 10));
        }

        return atendimento;
    }

    @Test
    @DisplayName("Deve retornar null quando atendimento fornecido for null")
    void deveRetornarNullQuandoAtendimentoForNull() {
        // Act
        AtendimentoResponse response = AtendimentoMapper.mapeamentoAtendimento(null);

        // Assert
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Deve mapear atendimento completo com dentista, endereço e finalizado")
    void deveMapearAtendimentoCompleto() {
        // Arrange
        Atendimento atendimento = criarAtendimento(1L, true, true);

        // Act
        AtendimentoResponse response = AtendimentoMapper.mapeamentoAtendimento(atendimento);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.prontuario()).isEqualTo("PRONT-001");
        assertThat(response.beneficiario()).isEqualTo("Carlos Silva");
        assertThat(response.dentista()).isEqualTo("Dra. Beatriz Santos");
        assertThat(response.contatoDentista()).isEqualTo("11988887777");
        assertThat(response.emailDentista()).isEqualTo("beatriz@odonto.com");
        assertThat(response.enderecoDentista()).isEqualTo("Rua das Flores, 123, São Paulo, SP");
        assertThat(response.dataInicial()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(response.dataFim()).isEqualTo("2026-02-10");
    }

    @Test
    @DisplayName("Deve preencher 'N/A' e 'NÃO FINALIZADO' quando dentista e data final forem nulos")
    void deveMapearAtendimentoSemDentistaENaoFinalizado() {
        // Arrange
        Atendimento atendimento = criarAtendimento(2L, false, false);
        atendimento.setBeneficiario(null);

        // Act
        AtendimentoResponse response = AtendimentoMapper.mapeamentoAtendimento(atendimento);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.beneficiario()).isEqualTo("N/A");
        assertThat(response.dentista()).isEqualTo("N/A");
        assertThat(response.contatoDentista()).isEqualTo("N/A");
        assertThat(response.emailDentista()).isEqualTo("N/A");
        assertThat(response.enderecoDentista()).isEqualTo("N/A");
        assertThat(response.dataFim()).isEqualTo("NÃO FINALIZADO");
    }

    @Test
    @DisplayName("Deve retornar null ao mapear lista nula de atendimentos")
    void deveRetornarNullAoMapearListaNula() {
        // Act
        List<AtendimentoResponse> lista = AtendimentoMapper.mapeamentoAtendimentos(null);

        // Assert
        assertThat(lista).isNull();
    }

    @Test
    @DisplayName("Deve mapear lista de atendimentos com sucesso")
    void deveMapearListaDeAtendimentos() {
        // Arrange
        Atendimento a1 = criarAtendimento(1L, true, true);
        Atendimento a2 = criarAtendimento(2L, false, false);

        // Act
        List<AtendimentoResponse> lista = AtendimentoMapper.mapeamentoAtendimentos(List.of(a1, a2));

        // Assert
        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).prontuario()).isEqualTo("PRONT-001");
        assertThat(lista.get(1).dataFim()).isEqualTo("NÃO FINALIZADO");
    }

    @Test
    @DisplayName("Deve mapear AtendimentoCreateRequest para entidade Atendimento")
    void deveMapearParaEntidade() {
        // Arrange
        AtendimentoCreateRequest request = new AtendimentoCreateRequest("PRONT-100", "12345678901", LocalDate.now());

        // Act
        Atendimento entidade = AtendimentoMapper.mapeamentoParaEntidade(request);

        // Assert
        assertThat(entidade).isNotNull();
        assertThat(entidade.getProntuario()).isEqualTo("PRONT-100");
        assertThat(entidade.getBeneficiario()).isNotNull();
        assertThat(entidade.getBeneficiario().getCpf()).isEqualTo("12345678901");
    }
}
