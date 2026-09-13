package br.com.raizdobem.api.service;

import br.com.raizdobem.api.entity.Especialidade;
import br.com.raizdobem.api.repository.EspecialidadeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - EspecialidadeService")
class EspecialidadeServiceTest {

    @Mock
    private EspecialidadeRepository repository;

    @InjectMocks
    private EspecialidadeService especialidadeService;

    @Test
    @DisplayName("Deve listar todas as especialidades cadastradas")
    void deveListarEspecialidades() {
        // Arrange
        Especialidade e1 = new Especialidade();
        e1.setId(1L);
        e1.setDescricao("Ortodontia");

        when(repository.listarTodas()).thenReturn(List.of(e1));

        // Act
        List<Especialidade> resultado = especialidadeService.listarEspecialidades();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getDescricao()).isEqualTo("Ortodontia");
    }

    @Test
    @DisplayName("Deve buscar especialidade por ID")
    void deveBuscarPorId() {
        // Arrange
        long id = 2L;
        Especialidade e = new Especialidade();
        e.setId(id);
        e.setDescricao("Endodontia");

        when(repository.findById(id)).thenReturn(e);

        // Act
        Especialidade resultado = especialidadeService.buscarPorId(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getDescricao()).isEqualTo("Endodontia");
    }
}
