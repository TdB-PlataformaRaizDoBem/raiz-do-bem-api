package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.entity.Especialidade;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.service.EspecialidadeService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - EspecialidadeResource")
class EspecialidadeResourceTest {

    @Mock
    private EspecialidadeService service;

    @InjectMocks
    private EspecialidadeResource resource;

    @Test
    @DisplayName("Deve listar todas as especialidades retornando HTTP 200")
    void deveListarTodasRetornandoStatus200() {
        // Arrange
        Especialidade e = new Especialidade();
        e.setId(1L);
        e.setDescricao("Ortodontia");
        when(service.listarEspecialidades()).thenReturn(List.of(e));

        // Act
        Response response = resource.listarTodas();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar especialidade por ID retornando HTTP 200")
    void deveBuscarPorIdRetornandoStatus200() {
        // Arrange
        long id = 1L;
        Especialidade e = new Especialidade();
        e.setId(id);
        e.setDescricao("Periodontia");
        when(service.buscarPorId(id)).thenReturn(e);

        // Act
        Response response = resource.buscarPorId(id);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(e);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando especialidade não existir por ID")
    void deveLancarNaoEncontradoExceptionQuandoIdNaoExistir() {
        // Arrange
        long idInexistente = 99L;
        when(service.buscarPorId(idInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.buscarPorId(idInexistente))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Especialidade não encontrada");
    }
}
