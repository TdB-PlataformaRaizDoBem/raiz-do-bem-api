package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.entity.ProgramaSocial;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ProgramaRepository")
class ProgramaRepositoryTest {

    @Spy
    private ProgramaRepository repository;

    @Test
    @DisplayName("Deve listar todos os programas sociais")
    void deveListarTodosOsProgramasSociais() {
        List<ProgramaSocial> esperado = List.of(new ProgramaSocial());
        doReturn(esperado).when(repository).listAll();

        List<ProgramaSocial> resultado = repository.listarTodos();

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar programa social por ID")
    void deveBuscarProgramaSocialPorId() {
        ProgramaSocial esperado = new ProgramaSocial();
        doReturn(esperado).when(repository).findById(2L);

        ProgramaSocial resultado = repository.buscarPorId(2L);

        assertThat(resultado).isSameAs(esperado);
    }
}
