package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.ColaboradorResponse;
import br.com.raizdobem.api.entity.Colaborador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - ColaboradorMapper")
class ColaboradorMapperTest {

    private Colaborador criarColaborador(Long id, String cpf, String nome, String email, String role) {
        Colaborador colaborador = new Colaborador();
        colaborador.setId(id);
        colaborador.setCpf(cpf);
        colaborador.setNomeCompleto(nome);
        colaborador.setDataNascimento(LocalDate.of(1992, 4, 10));
        colaborador.setDataContratacao(LocalDate.of(2021, 6, 1));
        colaborador.setEmail(email);
        colaborador.setSenha("hash_senha_secreta");
        colaborador.setRole(role);
        return colaborador;
    }

    @Test
    @DisplayName("Deve retornar null quando colaborador for nulo")
    void deveRetornarNullQuandoColaboradorForNulo() {
        // Act
        ColaboradorResponse response = ColaboradorMapper.mapeamentoParaResponse((Colaborador) null);

        // Assert
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Deve mapear colaborador para ColaboradorResponse com sucesso sem expor senha")
    void deveMapearColaboradorParaResponseComSucesso() {
        // Arrange
        Colaborador colaborador = criarColaborador(10L, "52998224725", "Lucas Ferreira", "lucas@raizdobem.org", "ADMIN");

        // Act
        ColaboradorResponse response = ColaboradorMapper.mapeamentoParaResponse(colaborador);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.cpf()).isEqualTo("52998224725");
        assertThat(response.nomeCompleto()).isEqualTo("Lucas Ferreira");
        assertThat(response.dataNascimento()).isEqualTo(LocalDate.of(1992, 4, 10));
        assertThat(response.dataContratacao()).isEqualTo(LocalDate.of(2021, 6, 1));
        assertThat(response.email()).isEqualTo("lucas@raizdobem.org");
        assertThat(response.role()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Deve retornar null ao mapear lista quando a lista for nula")
    void deveRetornarNullAoMapearListaNula() {
        // Act
        List<ColaboradorResponse> lista = ColaboradorMapper.mapeamentoParaResponse((List<Colaborador>) null);

        // Assert
        assertThat(lista).isNull();
    }

    @Test
    @DisplayName("Deve mapear lista vazia de colaboradores retornando lista vazia")
    void deveMapearListaVaziaDeColaboradores() {
        // Act
        List<ColaboradorResponse> lista = ColaboradorMapper.mapeamentoParaResponse(List.of());

        // Assert
        assertThat(lista).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve mapear lista de colaboradores com múltiplos elementos")
    void deveMapearListaColaboradoresComSucesso() {
        // Arrange
        Colaborador c1 = criarColaborador(1L, "52998224725", "Lucas Ferreira", "lucas@raizdobem.org", "ADMIN");
        Colaborador c2 = criarColaborador(2L, "12345678909", "Carla Souza", "carla@raizdobem.org", "COLABORADOR");

        // Act
        List<ColaboradorResponse> resultado = ColaboradorMapper.mapeamentoParaResponse(List.of(c1, c2));

        // Assert
        assertThat(resultado).isNotNull().hasSize(2);
        assertThat(resultado.get(0).id()).isEqualTo(1L);
        assertThat(resultado.get(0).nomeCompleto()).isEqualTo("Lucas Ferreira");
        assertThat(resultado.get(0).role()).isEqualTo("ADMIN");
        assertThat(resultado.get(1).id()).isEqualTo(2L);
        assertThat(resultado.get(1).nomeCompleto()).isEqualTo("Carla Souza");
        assertThat(resultado.get(1).role()).isEqualTo("COLABORADOR");
    }
}
