package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.EnderecoResponse;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.TipoEndereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários - EnderecoMapper")
class EnderecoMapperTest {

    private Endereco criarEndereco(Long id, TipoEndereco tipo) {
        Endereco e = new Endereco();
        e.setId(id);
        e.setLogradouro("Avenida Paulista");
        e.setCep("01311000");
        e.setNumero("1000");
        e.setBairro("Bela Vista");
        e.setCidade("São Paulo");
        e.setEstado("SP");
        e.setTipoEndereco(tipo);
        return e;
    }

    @Test
    @DisplayName("Deve instanciar construtor padrão")
    void deveInstanciarConstrutorPadrao() {
        EnderecoMapper mapper = new EnderecoMapper();
        assertThat(mapper).isNotNull();
    }

    @Test
    @DisplayName("Deve mapear entidade Endereco para EnderecoResponse com sucesso")
    void deveMapearEnderecoParaResponseComSucesso() {
        // Arrange
        Endereco endereco = criarEndereco(1L, TipoEndereco.RESIDENCIAL);

        // Act
        EnderecoResponse response = EnderecoMapper.mapeamentoParaResponse(endereco);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.logradouro()).isEqualTo("Avenida Paulista");
        assertThat(response.cep()).isEqualTo("01311000");
        assertThat(response.numero()).isEqualTo("1000");
        assertThat(response.bairro()).isEqualTo("Bela Vista");
        assertThat(response.cidade()).isEqualTo("São Paulo");
        assertThat(response.estado()).isEqualTo("SP");
        assertThat(response.tipoEndereco()).isEqualTo("RESIDENCIAL");
    }

    @Test
    @DisplayName("Deve mapear lista de entidades Endereco para lista de EnderecoResponse")
    void deveMapearListaDeEnderecosComSucesso() {
        // Arrange
        Endereco e1 = criarEndereco(1L, TipoEndereco.RESIDENCIAL);
        Endereco e2 = criarEndereco(2L, TipoEndereco.PROFISSIONAL);

        // Act
        List<EnderecoResponse> responses = EnderecoMapper.mapeamentoParaResponse(List.of(e1, e2));

        // Assert
        assertThat(responses).isNotNull().hasSize(2);
        assertThat(responses.get(0).tipoEndereco()).isEqualTo("RESIDENCIAL");
        assertThat(responses.get(1).tipoEndereco()).isEqualTo("PROFISSIONAL");
    }

    @Test
    @DisplayName("Deve mapear lista vazia para lista vazia de EnderecoResponse")
    void deveMapearListaVaziaComSucesso() {
        // Act
        List<EnderecoResponse> responses = EnderecoMapper.mapeamentoParaResponse(List.of());

        // Assert
        assertThat(responses).isNotNull().isEmpty();
    }
}
