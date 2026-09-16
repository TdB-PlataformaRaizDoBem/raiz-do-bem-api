package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.ColaboradorUpdateRequest;
import br.com.raizdobem.api.dto.request.ColaboradorCreateRequest;
import br.com.raizdobem.api.dto.response.ColaboradorResponse;
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

    private static final String CPF_VALIDO = "52998224725";

    @Mock
    private ColaboradorRepository repository;

    @InjectMocks
    private ColaboradorService colaboradorService;

    private ColaboradorCreateRequest criarColaboradorDTO(String cpf, String nome, LocalDate dataNasc, LocalDate dataContratacao) {
        return new ColaboradorCreateRequest(
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
        ColaboradorCreateRequest dto = criarColaboradorDTO(
                CPF_VALIDO,
                "Ana Paula Administradora",
                LocalDate.of(1990, 5, 10),
                LocalDate.of(2020, 1, 15)
        );

        when(repository.buscarPorCpf(CPF_VALIDO)).thenReturn(null);

        // Act
        ColaboradorResponse resultado = colaboradorService.criarColaborador(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.cpf()).isEqualTo(CPF_VALIDO);
        assertThat(resultado.nomeCompleto()).isEqualTo("Ana Paula Administradora");
        assertThat(resultado.email()).isEqualTo("admin@raizdobem.org");
        assertThat(resultado.role()).isEqualTo("ADMIN");

        ArgumentCaptor<Colaborador> captor = ArgumentCaptor.forClass(Colaborador.class);
        verify(repository, times(1)).criar(captor.capture());
        Colaborador salvo = captor.getValue();
        assertThat(BcryptUtil.matches("senha123", salvo.getSenha())).isTrue();
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao criar colaborador com CPF inválido")
    void deveLancarValidacaoExceptionAoCriarColaboradorComCpfInvalido() {
        // Arrange
        ColaboradorCreateRequest dto = criarColaboradorDTO(
                "1234",
                "Nome",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2020, 1, 1)
        );

        // Act & Assert
        assertThatThrownBy(() -> colaboradorService.criarColaborador(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Cpf inválido");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao criar colaborador com CPF já cadastrado")
    void deveLancarValidacaoExceptionAoCriarColaboradorComCpfDuplicado() {
        // Arrange
        ColaboradorCreateRequest dto = criarColaboradorDTO(
                CPF_VALIDO,
                "Nome",
                LocalDate.of(1990, 1, 1),
                LocalDate.of(2020, 1, 1)
        );

        when(repository.buscarPorCpf(CPF_VALIDO)).thenReturn(new Colaborador());

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
        ColaboradorCreateRequest dtoNulo = criarColaboradorDTO(CPF_VALIDO, null, LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1));
        ColaboradorCreateRequest dtoEmBranco = criarColaboradorDTO(CPF_VALIDO, "   ", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1));

        when(repository.buscarPorCpf(CPF_VALIDO)).thenReturn(null);

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
        ColaboradorCreateRequest dtoFuturo = criarColaboradorDTO(CPF_VALIDO, "Nome", LocalDate.now().plusDays(1), LocalDate.now());
        ColaboradorCreateRequest dtoNulo = criarColaboradorDTO(CPF_VALIDO, "Nome", null, LocalDate.now());

        when(repository.buscarPorCpf(CPF_VALIDO)).thenReturn(null);

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
        ColaboradorCreateRequest dtoFuturo = criarColaboradorDTO(CPF_VALIDO, "Nome", LocalDate.of(1990, 1, 1), LocalDate.now().plusDays(1));
        ColaboradorCreateRequest dtoNulo = criarColaboradorDTO(CPF_VALIDO, "Nome", LocalDate.of(1990, 1, 1), null);

        when(repository.buscarPorCpf(CPF_VALIDO)).thenReturn(null);

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
        String cpf = CPF_VALIDO;
        Colaborador colaborador = criarColaboradorEntidade(1L, cpf);
        ColaboradorUpdateRequest dto = new ColaboradorUpdateRequest("novo@raizdobem.org", "novaSenha456");

        when(repository.buscarPorCpf(cpf)).thenReturn(colaborador);

        // Act
        colaboradorService.atualizarColaborador(cpf, dto);

        // Assert
        ArgumentCaptor<ColaboradorUpdateRequest> captor = ArgumentCaptor.forClass(ColaboradorUpdateRequest.class);
        verify(repository, times(1)).atualizar(eq(cpf), captor.capture());

        ColaboradorUpdateRequest dtoEnviado = captor.getValue();
        assertThat(dtoEnviado.email()).isEqualTo("novo@raizdobem.org");
        assertThat(BcryptUtil.matches("novaSenha456", dtoEnviado.senha())).isTrue();
    }

    @Test
    @DisplayName("Deve manter email e senha antigos quando campos informados na atualização forem nulos ou vazios")
    void deveManterEmailESenhaAntigosQuandoCamposForemVazios() {
        // Arrange
        String cpf = CPF_VALIDO;
        Colaborador colaborador = criarColaboradorEntidade(1L, cpf);
        ColaboradorUpdateRequest dto = new ColaboradorUpdateRequest("", "  ");

        when(repository.buscarPorCpf(cpf)).thenReturn(colaborador);

        // Act
        colaboradorService.atualizarColaborador(cpf, dto);

        // Assert
        ArgumentCaptor<ColaboradorUpdateRequest> captor = ArgumentCaptor.forClass(ColaboradorUpdateRequest.class);
        verify(repository, times(1)).atualizar(eq(cpf), captor.capture());

        ColaboradorUpdateRequest dtoEnviado = captor.getValue();
        assertThat(dtoEnviado.email()).isEqualTo(colaborador.getEmail());
        assertThat(dtoEnviado.senha()).isEqualTo(colaborador.getSenha());
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao atualizar colaborador inexistente")
    void deveLancarNaoEncontradoExceptionAoAtualizarColaboradorInexistente() {
        // Arrange
        String cpf = "00000000000";
        ColaboradorUpdateRequest dto = new ColaboradorUpdateRequest("email@email.com", "senha");
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
        Colaborador c1 = criarColaboradorEntidade(1L, CPF_VALIDO);
        Colaborador c2 = criarColaboradorEntidade(2L, "12345678909");
        when(repository.listarTodos()).thenReturn(List.of(c1, c2));

        // Act
        List<ColaboradorResponse> lista = colaboradorService.listarTodos();

        // Assert
        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).cpf()).isEqualTo(CPF_VALIDO);
        assertThat(lista.get(0).nomeCompleto()).isEqualTo(c1.getNomeCompleto());
        assertThat(lista.get(1).cpf()).isEqualTo("12345678909");
    }

    @Test
    @DisplayName("Deve exibir colaborador por CPF")
    void deveExibirColaboradorPorCpfPorCpf() {
        // Arrange
        String cpf = CPF_VALIDO;
        Colaborador c = criarColaboradorEntidade(1L, cpf);
        when(repository.buscarPorCpf(cpf)).thenReturn(c);

        // Act
        ColaboradorResponse resultado = colaboradorService.exibirColaboradorPorCpf(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.cpf()).isEqualTo(cpf);
        assertThat(resultado.nomeCompleto()).isEqualTo(c.getNomeCompleto());
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao exibir colaborador por CPF inexistente")
    void deveLancarNaoEncontradoExceptionAoExibirColaboradorPorCpfInexistente() {
        // Arrange
        String cpf = "00000000000";
        when(repository.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> colaboradorService.exibirColaboradorPorCpf(cpf))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Colaborador não foi encontrado!");
    }

    @Test
    @DisplayName("Deve buscar colaborador por ID")
    void deveBuscarColaboradorPorId() {
        // Arrange
        long id = 5L;
        Colaborador c = criarColaboradorEntidade(id, CPF_VALIDO);
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
        String cpf = CPF_VALIDO;
        when(repository.excluir(cpf)).thenReturn(1L);

        // Act
        long resultado = colaboradorService.excluir(cpf);

        // Assert
        assertThat(resultado).isEqualTo(1L);
        verify(repository, times(1)).excluir(cpf);
    }
}
