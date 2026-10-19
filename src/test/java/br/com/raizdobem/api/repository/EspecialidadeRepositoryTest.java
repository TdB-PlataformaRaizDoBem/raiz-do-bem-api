package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.entity.Especialidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - EspecialidadeRepository")
class EspecialidadeRepositoryTest {

    @Spy
    private EspecialidadeRepository repository;

    @Test
    @DisplayName("Deve listar todas as especialidades")
    void deveListarTodasAsEspecialidades() {
        List<Especialidade> esperado = List.of(new Especialidade());
        doReturn(esperado).when(repository).listAll();

        List<Especialidade> resultado = repository.listarTodas();

        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    @DisplayName("Deve buscar especialidade por ID")
    void deveBuscarEspecialidadePorId() {
        Especialidade esperado = new Especialidade();
        doReturn(esperado).when(repository).findById(1L);

        Especialidade resultado = repository.buscarPorId(1L);

        assertThat(resultado).isSameAs(esperado);
    }
}
