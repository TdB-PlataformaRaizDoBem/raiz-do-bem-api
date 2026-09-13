package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtualizarColaboradorDTO;
import br.com.raizdobem.api.dto.request.CriarColaboradorDTO;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.ColaboradorRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - ColaboradorService")
class ColaboradorServiceTest {

    @Mock
    private ColaboradorRepository repository;

    @InjectMocks
    private ColaboradorService colaboradorService;

    private CriarColaboradorDTO criarColaboradorDTO(String cpf, String nome, LocalDate dataNasc, LocalDate dataContratacao) {
        return new CriarColaboradorDTO(
                cpf,
                nome,
                dataNasc,
                dataContratacao,
                "admin@raizdobem.org",
                "senha123",
                "ADMIN"
        );
    }

    private Colaborador criarColaboradorEntidade(Long id, String cpf) {
        Colaborador c = new Colaborador();
        c.setId(id);
        c.setCpf(cpf);
        c.setNomeCompleto("Ana Paula Administradora");
        c.setDataNascimento(LocalDate.of(1990, 5, 10));
        c.setDataContratacao(LocalDate.of(2020, 1, 15));
        c.setEmail("admin@raizdobem.org");
        c.setSenha(BcryptUtil.bcryptHash("senha123"));
        c.setRole("ADMIN");
        return c;
    }

    @Test
    @DisplayName("Deve criar colaborador com sucesso e hashear a senha quando dados forem válidos")
    void deveCriarColaboradorComSucessoQuandoDadosForemValidos() {
        // Arrange
        String cpf = "12345678901";
        CriarColaboradorDTO dto = criarColaboradorDTO(
                cpf,
                "Ana Paula Administradora",
                LocalDate.of(1990, 5, 10),
                LocalDate.of(2020, 1, 15)
        );

        when(repository.buscarPorCpf(cpf)).thenReturn(null);

        // Act
        Colaborador resultado = colaboradorService.criarColaborador(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCpf()).isEqualTo(cpf);
        assertThat(resultado.getNomeCompleto()).isEqualTo("Ana Paula Administradora");
        assertThat(BcryptUtil.matches("senha123", resultado.getSenha())).isTrue();

        verify(repository, times(1)).criar(any(Colaborador.class));
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao criar colaborador com CPF inválido")
    void deveLancarValidacaoExceptionAoCriarColaboradorComCpfInvalido() {
        // Arrange
        CriarColaboradorDTO dto = criarColaboradorDTO(
                "1234",
                "Nome",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2020, 1, 1)
        );

        // Act & Assert
        assertThatThrownBy(() -> colaboradorService.criarColaborador(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("CPF inserido é inválido");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao criar colaborador com CPF já cadastrado")
    void deveLancarValidacaoExceptionAoCriarColaboradorComCpfDuplicado() {
        // Arrange
        String cpf = "12345678901";
        CriarColaboradorDTO dto = criarColaboradorDTO(
                cpf,
                "Nome",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2020, 1, 1)
        );

        when(repository.buscarPorCpf(cpf)).thenReturn(new Colaborador());

        // Act & Assert
        assertThatThrownBy(() -> colaboradorService.criarColaborador(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Já existe um colaborador com esse CPF");

        verify(repository, never()).criar(any());
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao criar colaborador com nome nulo ou em branco")
    void deveLancarNaoEncontradoExceptionAoCriarComNomeInvalido() {
        // Arrange
        String cpf = "12345678901";
        CriarColaboradorDTO dtoNulo = criarColaboradorDTO(cpf, null, LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1));
        CriarColaboradorDTO dtoEmBranco = criarColaboradorDTO(cpf, "   ", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1));

        when(repository.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> colaboradorService.criarColaborador(dtoNulo))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Nome completo deve ser inserido.");

        assertThatThrownBy(() -> colaboradorService.criarColaborador(dtoEmBranco))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Nome completo deve ser inserido.");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao criar colaborador com data de nascimento futura ou nula")
    void deveLancarNaoEncontradoExceptionAoCriarComDataNascimentoInvalida() {
        // Arrange
        String cpf = "12345678901";
        CriarColaboradorDTO dtoFuturo = criarColaboradorDTO(cpf, "Nome", LocalDate.now().plusDays(1), LocalDate.now());
        CriarColaboradorDTO dtoNulo = criarColaboradorDTO(cpf, "Nome", null, LocalDate.now());

        when(repository.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> colaboradorService.criarColaborador(dtoFuturo))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Data de nascimento inválida.");

        assertThatThrownBy(() -> colaboradorService.criarColaborador(dtoNulo))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Data de nascimento inválida.");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao criar colaborador com data de contratação futura ou nula")
    void deveLancarNaoEncontradoExceptionAoCriarComDataContratacaoInvalida() {
        // Arrange
        String cpf = "12345678901";
        CriarColaboradorDTO dtoFuturo = criarColaboradorDTO(cpf, "Nome", LocalDate.of(1990, 1, 1), LocalDate.now().plusDays(1));
        CriarColaboradorDTO dtoNulo = criarColaboradorDTO(cpf, "Nome", LocalDate.of(1990, 1, 1), null);

        when(repository.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> colaboradorService.criarColaborador(dtoFuturo))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Data de contratação inválida.");

        assertThatThrownBy(() -> colaboradorService.criarColaborador(dtoNulo))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Data de contratação inválida.");
    }

    @Test
    @DisplayName("Deve atualizar colaborador com sucesso com novo email e nova senha hasheada")
    void deveAtualizarColaboradorComNovoEmailESenha() {
        // Arrange
        String cpf = "12345678901";
        Colaborador colaborador = criarColaboradorEntidade(1L, cpf);
        AtualizarColaboradorDTO dto = new AtualizarColaboradorDTO("novo@raizdobem.org", "novaSenha456");

        when(repository.buscarPorCpf(cpf)).thenReturn(colaborador);

        // Act
        colaboradorService.atualizarColaborador(cpf, dto);

        // Assert
        ArgumentCaptor<AtualizarColaboradorDTO> captor = ArgumentCaptor.forClass(AtualizarColaboradorDTO.class);
        verify(repository, times(1)).atualizar(eq(cpf), captor.capture());

        AtualizarColaboradorDTO dtoEnviado = captor.getValue();
        assertThat(dtoEnviado.email()).isEqualTo("novo@raizdobem.org");
        assertThat(BcryptUtil.matches("novaSenha456", dtoEnviado.senha())).isTrue();
    }

    @Test
    @DisplayName("Deve manter email e senha antigos quando campos informados na atualização forem nulos ou vazios")
    void deveManterEmailESenhaAntigosQuandoCamposForemVazios() {
        // Arrange
        String cpf = "12345678901";
        Colaborador colaborador = criarColaboradorEntidade(1L, cpf);
        AtualizarColaboradorDTO dto = new AtualizarColaboradorDTO("", "  ");

        when(repository.buscarPorCpf(cpf)).thenReturn(colaborador);

        // Act
        colaboradorService.atualizarColaborador(cpf, dto);

        // Assert
        ArgumentCaptor<AtualizarColaboradorDTO> captor = ArgumentCaptor.forClass(AtualizarColaboradorDTO.class);
        verify(repository, times(1)).atualizar(eq(cpf), captor.capture());

        AtualizarColaboradorDTO dtoEnviado = captor.getValue();
        assertThat(dtoEnviado.email()).isEqualTo(colaborador.getEmail());
        assertThat(dtoEnviado.senha()).isEqualTo(colaborador.getSenha());
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao atualizar colaborador inexistente")
    void deveLancarNaoEncontradoExceptionAoAtualizarColaboradorInexistente() {
        // Arrange
        String cpf = "00000000000";
        AtualizarColaboradorDTO dto = new AtualizarColaboradorDTO("email@email.com", "senha");
        when(repository.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> colaboradorService.atualizarColaborador(cpf, dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Colaborador não encontrado");

        verify(repository, never()).atualizar(any(), any());
    }

    @Test
    @DisplayName("Deve listar todos os colaboradores")
    void deveListarTodosOsColaboradores() {
        // Arrange
        when(repository.listarTodos()).thenReturn(List.of(new Colaborador(), new Colaborador()));

        // Act
        List<Colaborador> lista = colaboradorService.listarTodos();

        // Assert
        assertThat(lista).hasSize(2);
    }

    @Test
    @DisplayName("Deve exibir colaborador por CPF")
    void deveExibirColaboradorPorCpf() {
        // Arrange
        String cpf = "12345678901";
        Colaborador c = criarColaboradorEntidade(1L, cpf);
        when(repository.buscarPorCpf(cpf)).thenReturn(c);

        // Act
        Colaborador resultado = colaboradorService.exibirColaborador(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCpf()).isEqualTo(cpf);
    }

    @Test
    @DisplayName("Deve buscar colaborador por ID")
    void deveBuscarColaboradorPorId() {
        // Arrange
        long id = 5L;
        Colaborador c = criarColaboradorEntidade(id, "12345678901");
        when(repository.buscarPorId(id)).thenReturn(c);

        // Act
        Colaborador resultado = colaboradorService.buscarPorId(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve excluir colaborador por CPF")
    void deveExcluirColaboradorPorCpf() {
        // Arrange
        String cpf = "12345678901";
        when(repository.excluir(cpf)).thenReturn(1L);

        // Act
        long resultado = colaboradorService.excluir(cpf);

        // Assert
        assertThat(resultado).isEqualTo(1L);
        verify(repository, times(1)).excluir(cpf);
    }
}
