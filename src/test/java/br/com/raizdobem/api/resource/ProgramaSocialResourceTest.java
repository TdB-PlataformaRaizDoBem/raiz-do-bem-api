package br.com.raizdobem.api.resource;

import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.service.ProgramaService;
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
@DisplayName("Testes Unitários - ProgramaSocialResource")
class ProgramaSocialResourceTest {

    @Mock
    private ProgramaService service;

    @InjectMocks
    private ProgramaSocialResource resource;

    @Test
    @DisplayName("Deve listar todos os programas sociais retornando HTTP 200")
    void deveListarTodosRetornandoStatus200() {
        // Arrange
        ProgramaSocial p = new ProgramaSocial();
        p.setId(1L);
        p.setPrograma("Dentista do Bem");
        when(service.listarProgramasSociais()).thenReturn(List.of(p));

        // Act
        Response response = resource.listarTodos();

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar programa social por ID retornando HTTP 200")
    void deveBuscarPorIdRetornandoStatus200() {
        // Arrange
        long id = 1L;
        ProgramaSocial p = new ProgramaSocial();
        p.setId(id);
        p.setPrograma("Apolônias do Bem");
        when(service.buscarPorId(id)).thenReturn(p);

        // Act
        Response response = resource.buscarPorId(id);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity()).isEqualTo(p);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando programa social não existir por ID")
    void deveLancarNaoEncontradoExceptionQuandoIdNaoExistir() {
        // Arrange
        long idInexistente = 99L;
        when(service.buscarPorId(idInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> resource.buscarPorId(idInexistente))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Programa social com id " + idInexistente + " não encontrado.");
    }
}
