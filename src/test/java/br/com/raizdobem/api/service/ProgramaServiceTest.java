package br.com.raizdobem.api.service;

import br.com.raizdobem.api.entity.ProgramaSocial;
import br.com.raizdobem.api.repository.ProgramaRepository;
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
@DisplayName("Testes Unitários - ProgramaService")
class ProgramaServiceTest {

    @Mock
    private ProgramaRepository repository;

    @InjectMocks
    private ProgramaService programaService;

    @Test
    @DisplayName("Deve listar todos os programas sociais")
    void deveListarProgramasSociais() {
        // Arrange
        ProgramaSocial p = new ProgramaSocial();
        p.setId(1L);
        p.setPrograma("Dentista do Bem");

        when(repository.listarTodos()).thenReturn(List.of(p));

        // Act
        List<ProgramaSocial> lista = programaService.listarProgramasSociais();

        // Assert
        assertThat(lista).hasSize(1);
        assertThat(lista.getFirst().getPrograma()).isEqualTo("Dentista do Bem");
    }

    @Test
    @DisplayName("Deve buscar programa social por ID")
    void deveBuscarPorId() {
        // Arrange
        long id = 1L;
        ProgramaSocial p = new ProgramaSocial();
        p.setId(id);
        p.setPrograma("Dentista do Bem");

        when(repository.findById(id)).thenReturn(p);

        // Act
        ProgramaSocial resultado = programaService.buscarPorId(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getPrograma()).isEqualTo("Dentista do Bem");
    }
}
