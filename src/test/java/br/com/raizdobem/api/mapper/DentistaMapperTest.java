package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.DentistaResponse;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.Especialidade;
import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.entity.Sexo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - DentistaMapper")
class DentistaMapperTest {

    private Dentista criarDentista(Long id, boolean comRelacionamentos) {
        Dentista d = new Dentista();
        d.setId(id);
        d.setCroDentista("123456");
        d.setCpf("12345678901");
        d.setNomeCompleto("Dra. Camila Santos");
        d.setSexo(Sexo.F);
        d.setEmail("camila@odonto.com");
        d.setTelefone("11988887777");
        d.setCategoria("COORDENADOR");
        d.setDisponivel("true");

        if (comRelacionamentos) {
            Especialidade esp1 = new Especialidade();
            esp1.setId(1L);
            esp1.setDescricao("Ortodontia");

            Especialidade esp2 = new Especialidade();
            esp2.setId(2L);
            esp2.setDescricao("Endodontia");

            d.setEspecialidades(List.of(esp1, esp2));

            ProgramaSocial p1 = new ProgramaSocial();
            p1.setId(1L);
            p1.setPrograma("Programa Sorriso Aberto");

            d.setProgramasSociais(List.of(p1));

            Endereco endereco = new Endereco();
            endereco.setLogradouro("Rua das Flores");
            endereco.setNumero("100");
            endereco.setCidade("São Paulo");
            endereco.setEstado("SP");
            endereco.setCep("01001000");

            d.setEndereco(endereco);
        }

        return d;
    }

    @Test
    @DisplayName("Deve retornar null quando dentista fornecido for null")
    void deveRetornarNullQuandoDentistaForNull() {
        // Act
        DentistaResponse response = DentistaMapper.mapeamentoParaResponse((Dentista) null);

        // Assert
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Deve mapear dentista completo com especialidades, programas e endereço")
    void deveMapearDentistaCompleto() {
        // Arrange
        Dentista dentista = criarDentista(1L, true);

        // Act
        DentistaResponse response = DentistaMapper.mapeamentoParaResponse(dentista);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.croDentista()).isEqualTo("123456");
        assertThat(response.cpf()).isEqualTo("12345678901");
        assertThat(response.nomeCompleto()).isEqualTo("Dra. Camila Santos");
        assertThat(response.sexo()).isEqualTo("F");
        assertThat(response.email()).isEqualTo("camila@odonto.com");
        assertThat(response.telefone()).isEqualTo("11988887777");
        assertThat(response.categoria()).isEqualTo("COORDENADOR");
        assertThat(response.disponivel()).isEqualTo("true");
        assertThat(response.especialidades()).containsExactly("Ortodontia", "Endodontia");
        assertThat(response.programasSociais()).containsExactly("Programa Sorriso Aberto");
        assertThat(response.logradouro()).isEqualTo("Rua das Flores");
        assertThat(response.numero()).isEqualTo("100");
        assertThat(response.cidade()).isEqualTo("São Paulo");
        assertThat(response.estado()).isEqualTo("SP");
        assertThat(response.cep()).isEqualTo("01001000");
    }

    @Test
    @DisplayName("Deve mapear dentista com especialidades, programas e endereço nulos")
    void deveMapearDentistaComCamposNulos() {
        // Arrange
        Dentista dentista = criarDentista(2L, false);
        dentista.setSexo(null);

        // Act
        DentistaResponse response = DentistaMapper.mapeamentoParaResponse(dentista);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.sexo()).isEqualTo("N/A");
        assertThat(response.especialidades()).isEmpty();
        assertThat(response.programasSociais()).isEmpty();
        assertThat(response.logradouro()).isNull();
        assertThat(response.cidade()).isNull();
    }

    @Test
    @DisplayName("Deve retornar null ao mapear lista de dentistas nula")
    void deveRetornarNullAoMapearListaNula() {
        // Act
        List<DentistaResponse> lista = DentistaMapper.mapeamentoParaResponse((List<Dentista>) null);

        // Assert
        assertThat(lista).isNull();
    }

    @Test
    @DisplayName("Deve mapear lista de dentistas com sucesso")
    void deveMapearListaDeDentistas() {
        // Arrange
        Dentista d1 = criarDentista(1L, true);
        Dentista d2 = criarDentista(2L, false);

        // Act
        List<DentistaResponse> lista = DentistaMapper.mapeamentoParaResponse(List.of(d1, d2));

        // Assert
        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).nomeCompleto()).isEqualTo("Dra. Camila Santos");
        assertThat(lista.get(1).especialidades()).isEmpty();
    }
}
