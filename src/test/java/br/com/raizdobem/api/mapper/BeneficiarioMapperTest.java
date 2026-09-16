package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.BeneficiarioResponse;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.PedidoAjuda;
import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.entity.TipoEndereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - BeneficiarioMapper")
class BeneficiarioMapperTest {

    private Beneficiario criarBeneficiario(Long id, boolean comRelacionamentos) {
        Beneficiario b = new Beneficiario();
        b.setId(id);
        b.setCpf("12345678901");
        b.setNomeCompleto("Ana Maria");
        b.setDataNascimento(LocalDate.of(2010, 5, 20));
        b.setTelefone("11988887777");
        b.setEmail("ana@email.com");

        if (comRelacionamentos) {
            PedidoAjuda pedido = new PedidoAjuda();
            pedido.setId(5L);
            Dentista dentista = new Dentista();
            dentista.setNomeCompleto("Dr. Lucas");
            pedido.setDentista(dentista);
            b.setPedido(pedido);

            ProgramaSocial programa = new ProgramaSocial();
            programa.setId(2L);
            programa.setPrograma("Sorriso Infantil");
            b.setProgramaSocial(programa);

            Endereco endereco = new Endereco();
            endereco.setId(10L);
            endereco.setLogradouro("Av Brasil");
            endereco.setCep("01001000");
            endereco.setNumero("50");
            endereco.setBairro("Centro");
            endereco.setCidade("São Paulo");
            endereco.setEstado("SP");
            endereco.setTipoEndereco(TipoEndereco.RESIDENCIAL);
            b.setEndereco(endereco);
        }

        return b;
    }

    @Test
    @DisplayName("Deve retornar null quando beneficiário for null")
    void deveRetornarNullQuandoBeneficiarioForNull() {
        // Act
        BeneficiarioResponse response = BeneficiarioMapper.mapeamentoBeneficiario(null);

        // Assert
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Deve mapear beneficiário completo com pedido, programa social e endereço")
    void deveMapearBeneficiarioCompleto() {
        // Arrange
        Beneficiario b = criarBeneficiario(1L, true);

        // Act
        BeneficiarioResponse response = BeneficiarioMapper.mapeamentoBeneficiario(b);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.cpf()).isEqualTo("12345678901");
        assertThat(response.nomeCompleto()).isEqualTo("Ana Maria");
        assertThat(response.pedido()).isNotNull();
        assertThat(response.pedido().id()).isEqualTo(5L);
        assertThat(response.pedido().dentistaResponsavel()).isEqualTo("Dr. Lucas");
        assertThat(response.programaSocial()).isEqualTo("Sorriso Infantil");
        assertThat(response.endereco()).isNotNull();
        assertThat(response.endereco().cidade()).isEqualTo("São Paulo");
        assertThat(response.endereco().tipoEndereco()).isEqualTo("RESIDENCIAL");
    }

    @Test
    @DisplayName("Deve mapear beneficiário sem pedido, programa e endereço preenchendo defaults")
    void deveMapearBeneficiarioSemRelacionamentos() {
        // Arrange
        Beneficiario b = criarBeneficiario(2L, false);

        // Act
        BeneficiarioResponse response = BeneficiarioMapper.mapeamentoBeneficiario(b);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.pedido()).isNull();
        assertThat(response.programaSocial()).isEqualTo("N/A");
        assertThat(response.endereco()).isNull();
    }

    @Test
    @DisplayName("Deve retornar null ao mapear lista nula de beneficiários")
    void deveRetornarNullAoMapearListaNula() {
        // Act
        List<BeneficiarioResponse> lista = BeneficiarioMapper.mapeamentoBeneficiarios(null);

        // Assert
        assertThat(lista).isNull();
    }

    @Test
    @DisplayName("Deve mapear lista de beneficiários com sucesso")
    void deveMapearListaDeBeneficiarios() {
        // Arrange
        Beneficiario b1 = criarBeneficiario(1L, true);
        Beneficiario b2 = criarBeneficiario(2L, false);

        // Act
        List<BeneficiarioResponse> lista = BeneficiarioMapper.mapeamentoBeneficiarios(List.of(b1, b2));

        // Assert
        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).nomeCompleto()).isEqualTo("Ana Maria");
        assertThat(lista.get(1).programaSocial()).isEqualTo("N/A");
    }
}
