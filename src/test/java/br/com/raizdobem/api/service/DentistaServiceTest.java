package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.DentistaUpdateRequest;
import br.com.raizdobem.api.dto.request.DentistaCreateRequest;
import br.com.raizdobem.api.dto.request.EnderecoRequest;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.entity.*;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.DentistaRepository;
import br.com.raizdobem.api.repository.EspecialidadeRepository;
import br.com.raizdobem.api.repository.ProgramaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - DentistaService")
class DentistaServiceTest {

    private static final String CPF_VALIDO = "52998224725";

    @Mock
    private DentistaRepository repository;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @Mock
    private ProgramaRepository programaRepository;

    @InjectMocks
    private DentistaService dentistaService;

    private EnderecoRequest criarEntradaEnderecoDTO() {
        return new EnderecoRequest("01310100", "1000");
    }

    private Endereco criarEndereco() {
        Endereco e = new Endereco();
        e.setId(1L);
        e.setTipoEndereco(TipoEndereco.PROFISSIONAL);
        e.setCep("01310100");
        e.setLogradouro("Avenida Paulista");
        e.setNumero("1000");
        e.setBairro("Bela Vista");
        e.setCidade("São Paulo");
        e.setEstado("SP");
        return e;
    }

    private Especialidade criarEspecialidade(Long id, String nome) {
        Especialidade esp = new Especialidade();
        esp.setId(id);
        esp.setDescricao(nome);
        return esp;
    }

    private ProgramaSocial criarPrograma(Long id, String nome) {
        ProgramaSocial p = new ProgramaSocial();
        p.setId(id);
        p.setPrograma(nome);
        return p;
    }

    private Dentista criarDentista(Long id, String cpf) {
        Dentista d = new Dentista();
        d.setId(id);
        d.setCpf(cpf);
        d.setCroDentista("123456");
        d.setNomeCompleto("Dr. Marcos Vinicius");
        d.setSexo(Sexo.M);
        d.setEmail("marcos@odonto.com");
        d.setTelefone("11988887777");
        d.setCategoria("COORDENADOR");
        d.setDisponivel("true");
        d.setEndereco(criarEndereco());
        d.setEspecialidades(List.of(criarEspecialidade(1L, "Endodontia")));
        d.setProgramasSociais(List.of(criarPrograma(1L, "Programa 1"), criarPrograma(2L, "Programa 2")));
        return d;
    }

    @Test
    @DisplayName("Deve criar dentista com sucesso quando dados forem válidos")
    void deveCriarDentistaComSucessoQuandoDadosForemValidos() {
        // Arrange
        DentistaCreateRequest dto = new DentistaCreateRequest(
                "123456",
                CPF_VALIDO,
                "Dr. Marcos Vinicius",
                "M",
                "marcos@odonto.com",
                "11988887777",
                "COORDENADOR",
                1L,
                "true",
                criarEntradaEnderecoDTO()
        );

        Especialidade esp = criarEspecialidade(1L, "Endodontia");
        ProgramaSocial p1 = criarPrograma(1L, "Programa 1");
        ProgramaSocial p2 = criarPrograma(2L, "Programa 2");
        Endereco endereco = criarEndereco();

        when(especialidadeRepository.buscarPorId(1L)).thenReturn(esp);
        when(programaRepository.listarTodos()).thenReturn(List.of(p1, p2));
        when(enderecoService.criarComoSuporte(dto.endereco(), TipoEndereco.PROFISSIONAL)).thenReturn(endereco);

        // Act
        DentistaDTO resultado = dentistaService.criarDentista(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.nomeCompleto()).isEqualTo("Dr. Marcos Vinicius");
        assertThat(resultado.croDentista()).isEqualTo("123456");
        assertThat(resultado.categoria()).isEqualTo("COORDENADOR");

        verify(repository, times(1)).criar(any(Dentista.class));
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao criar dentista com CPF inválido")
    void deveLancarValidacaoExceptionAoCriarDentistaComCpfInvalido() {
        // Arrange
        DentistaCreateRequest dto = new DentistaCreateRequest(
                "123456",
                "12345",
                "Dr. Marcos",
                "M",
                "marcos@email.com",
                "11988887777",
                "VOLUNTARIO",
                1L,
                "true",
                criarEntradaEnderecoDTO()
        );

        // Act & Assert
        assertThatThrownBy(() -> dentistaService.criarDentista(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("CPF inserido é inválido");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando especialidade informada não for encontrada")
    void deveLancarNaoEncontradoExceptionQuandoEspecialidadeNaoExistir() {
        // Arrange
        DentistaCreateRequest dto = new DentistaCreateRequest(
                "123456",
                CPF_VALIDO,
                "Dr. Marcos",
                "M",
                "marcos@email.com",
                "11988887777",
                "VOLUNTARIO",
                99L,
                "true",
                criarEntradaEnderecoDTO()
        );

        when(especialidadeRepository.buscarPorId(99L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> dentistaService.criarDentista(dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Especialidade não encontrada.");

        verifyNoInteractions(programaRepository);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando endereço do dentista for nulo")
    void deveLancarNaoEncontradoExceptionQuandoEnderecoForNulo() {
        // Arrange
        DentistaCreateRequest dto = new DentistaCreateRequest(
                "123456",
                CPF_VALIDO,
                "Dr. Marcos",
                "M",
                "marcos@email.com",
                "11988887777",
                "VOLUNTARIO",
                1L,
                "true",
                criarEntradaEnderecoDTO()
        );

        when(especialidadeRepository.buscarPorId(1L)).thenReturn(criarEspecialidade(1L, "Endodontia"));
        when(programaRepository.listarTodos()).thenReturn(List.of(criarPrograma(1L, "P1"), criarPrograma(2L, "P2")));
        when(enderecoService.criarComoSuporte(dto.endereco(), TipoEndereco.PROFISSIONAL)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> dentistaService.criarDentista(dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Endereço não encontrado!");

        verify(repository, never()).criar(any());
    }

    @Test
    @DisplayName("Deve buscar dentista por ID com sucesso")
    void deveBuscarDentistaPorIdComSucesso() {
        // Arrange
        long id = 10L;
        Dentista dentista = criarDentista(id, CPF_VALIDO);
        when(repository.findById(id)).thenReturn(dentista);

        // Act
        DentistaDTO resultado = dentistaService.buscarPorId(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao buscar por ID inexistente")
    void deveLancarNaoEncontradoExceptionAoBuscarPorIdInexistente() {
        // Arrange
        long idInexistente = 99L;
        when(repository.findById(idInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> dentistaService.buscarPorId(idInexistente))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Dentista não encontrado.");
    }

    @Test
    @DisplayName("Deve exibir dentista por CPF com sucesso")
    void deveExibirDentistaPorCpfComSucesso() {
        // Arrange
        String cpf = CPF_VALIDO;
        Dentista dentista = criarDentista(1L, cpf);
        when(repository.buscarPorCpf(cpf)).thenReturn(dentista);

        // Act
        DentistaDTO resultado = dentistaService.exibirDentista(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.nomeCompleto()).isEqualTo("Dr. Marcos Vinicius");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao exibir dentista por CPF inexistente")
    void deveLancarNaoEncontradoExceptionAoExibirPorCpfInexistente() {
        // Arrange
        String cpfInexistente = "00000000000";
        when(repository.buscarPorCpf(cpfInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> dentistaService.exibirDentista(cpfInexistente))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Dentista não encontrado.");
    }

    @Test
    @DisplayName("Deve listar todos os dentistas")
    void deveListarTodosOsDentistas() {
        // Arrange
        Dentista d = criarDentista(1L, CPF_VALIDO);
        when(repository.listarTodos()).thenReturn(List.of(d));

        // Act
        List<DentistaDTO> resultado = dentistaService.listarTodos();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar apenas dentistas disponíveis")
    void deveListarDentistasDisponiveis() {
        // Arrange
        Dentista d = criarDentista(1L, CPF_VALIDO);
        when(repository.listarDisponiveis()).thenReturn(List.of(d));

        // Act
        List<DentistaDTO> resultado = dentistaService.listarDisponiveis();

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar dentistas por cidade")
    void deveListarDentistasPorCidade() {
        // Arrange
        Dentista d = criarDentista(1L, CPF_VALIDO);
        when(repository.listarPorCidade("São Paulo")).thenReturn(List.of(d));

        // Act
        List<DentistaDTO> resultado = dentistaService.listarPorCidades("São Paulo");

        // Assert
        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar dentista e atualizar endereço residencial")
    void deveAtualizarDentistaComSucesso() {
        // Arrange
        String cpf = CPF_VALIDO;
        EnderecoRequest novoEndereco = new EnderecoRequest("01001000", "500");
        DentistaUpdateRequest request = new DentistaUpdateRequest("11911112222", "novo@email.com", "VOLUNTARIO", 1L, "false", novoEndereco);
        Dentista dentista = criarDentista(1L, cpf);

        when(repository.buscarPorCpf(cpf)).thenReturn(dentista);

        // Act
        DentistaDTO resultado = dentistaService.atualizar(cpf, request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(dentista.getTelefone()).isEqualTo("11911112222");
        assertThat(dentista.getEmail()).isEqualTo("novo@email.com");
        assertThat(dentista.getCategoria()).isEqualTo("VOLUNTARIO");
        assertThat(dentista.getDisponivel()).isEqualTo("false");

        verify(enderecoService, times(1)).entradaEndereco(
                eq(dentista.getEndereco()),
                eq(novoEndereco),
                eq(TipoEndereco.RESIDENCIAL)
        );
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao atualizar dentista que não existe")
    void deveLancarNaoEncontradoExceptionAoAtualizarDentistaInexistente() {
        // Arrange
        String cpf = "00000000000";
        DentistaUpdateRequest request = new DentistaUpdateRequest("11911112222", "novo@email.com", "VOLUNTARIO", 1L, "true", null);
        when(repository.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> dentistaService.atualizar(cpf, request))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Dentista não encontrado.");

        verifyNoInteractions(enderecoService);
    }

    @Test
    @DisplayName("Deve excluir dentista por CPF")
    void deveExcluirDentistaPorCpf() {
        // Arrange
        String cpf = CPF_VALIDO;
        when(repository.excluir(cpf)).thenReturn(1L);

        // Act
        long resultado = dentistaService.excluir(cpf);

        // Assert
        assertThat(resultado).isEqualTo(1L);
        verify(repository, times(1)).excluir(cpf);
    }

    @Test
    @DisplayName("Deve listar dentistas para exportação")
    void deveListarDentistasParaExportacao() {
        // Arrange
        Dentista d = criarDentista(1L, CPF_VALIDO);
        when(repository.listarTodos()).thenReturn(List.of(d));

        // Act
        List<DentistaDTO> resultado = dentistaService.listarParaExportacao();

        // Assert
        assertThat(resultado).hasSize(1);
    }
}
