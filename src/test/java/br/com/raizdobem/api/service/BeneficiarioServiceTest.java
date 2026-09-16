package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.BeneficiarioUpdateRequest;
import br.com.raizdobem.api.dto.request.BeneficiarioCreateRequest;
import br.com.raizdobem.api.dto.request.EnderecoRequest;
import br.com.raizdobem.api.dto.response.BeneficiarioResponse;
import br.com.raizdobem.api.entity.*;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.BeneficiarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("Testes Unitários - BeneficiarioService")
class BeneficiarioServiceTest {

    private static final String CPF_VALIDO = "52998224725";

    @Mock
    private BeneficiarioRepository repository;

    @Mock
    private PedidoAjudaService pedidoAjudaService;

    @Mock
    private ProgramaService programaService;

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private BeneficiarioService beneficiarioService;

    private Endereco criarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        endereco.setCep("01001000");
        endereco.setLogradouro("Praça da Sé");
        endereco.setNumero("100");
        endereco.setBairro("Sé");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        return endereco;
    }

    private PedidoAjuda criarPedido(Long id, StatusPedido status) {
        PedidoAjuda pedido = new PedidoAjuda();
        pedido.setId(id);
        pedido.setCpf(CPF_VALIDO);
        pedido.setNomeCompleto("Maria Joana da Silva");
        pedido.setDataNascimento(LocalDate.of(2012, 3, 15));
        pedido.setTelefone("11987654321");
        pedido.setEmail("maria@email.com");
        pedido.setStatus(status);
        pedido.setEndereco(criarEndereco());
        return pedido;
    }

    private ProgramaSocial criarPrograma(Long id, String nome) {
        ProgramaSocial programa = new ProgramaSocial();
        programa.setId(id);
        programa.setPrograma(nome);
        return programa;
    }

    private Beneficiario criarBeneficiario(Long id) {
        Beneficiario b = new Beneficiario();
        b.setId(id);
        b.setCpf(CPF_VALIDO);
        b.setNomeCompleto("Maria Joana da Silva");
        b.setDataNascimento(LocalDate.of(2012, 3, 15));
        b.setTelefone("11987654321");
        b.setEmail("maria@email.com");
        b.setEndereco(criarEndereco());
        b.setProgramaSocial(criarPrograma(1L, "Sorriso Aberto"));
        return b;
    }

    @Test
    @DisplayName("Deve criar beneficiário com sucesso quando pedido estiver APROVADO e programa social existir")
    void deveCriarBeneficiarioComSucessoQuandoDadosForemValidos() {
        // Arrange
        BeneficiarioCreateRequest dto = new BeneficiarioCreateRequest(10L, 1L);
        PedidoAjuda pedidoAprovado = criarPedido(10L, StatusPedido.APROVADO);
        ProgramaSocial programaSocial = criarPrograma(1L, "Sorriso Criança");

        when(pedidoAjudaService.buscarEntidadePorId(10L)).thenReturn(pedidoAprovado);
        when(programaService.buscarPorId(1L)).thenReturn(programaSocial);

        // Act
        BeneficiarioResponse resultado = beneficiarioService.criarBeneficiario(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.cpf()).isEqualTo(CPF_VALIDO);
        assertThat(resultado.nomeCompleto()).isEqualTo("Maria Joana da Silva");
        assertThat(resultado.programaSocial()).isEqualTo("Sorriso Criança");

        verify(repository, times(1)).criar(any(Beneficiario.class));
    }

    @Test
    @DisplayName("Deve lançar RequisicaoInvalidaException quando DTO for nulo")
    void deveLancarRequisicaoInvalidaExceptionQuandoDtoForNulo() {
        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.criarBeneficiario((BeneficiarioCreateRequest) null))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Inserção de beneficiário inválida.");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando pedido de ajuda informado não existir")
    void deveLancarNaoEncontradoExceptionQuandoPedidoNaoExistir() {
        // Arrange
        BeneficiarioCreateRequest dto = new BeneficiarioCreateRequest(99L, 1L);
        when(pedidoAjudaService.buscarEntidadePorId(99L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.criarBeneficiario(dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Não foi possível encontrar pedido informado.");

        verifyNoInteractions(programaService);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar RequisicaoInvalidaException quando pedido de ajuda não estiver APROVADO")
    void deveLancarRequisicaoInvalidaExceptionQuandoPedidoNaoEstiverAprovado() {
        // Arrange
        BeneficiarioCreateRequest dto = new BeneficiarioCreateRequest(10L, 1L);
        PedidoAjuda pedidoPendente = criarPedido(10L, StatusPedido.PENDENTE);
        when(pedidoAjudaService.buscarEntidadePorId(10L)).thenReturn(pedidoPendente);

        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.criarBeneficiario(dto))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Pedido de ajuda não foi APROVADO. Impossível seguir o processo de criação de beneficiário.");

        verifyNoInteractions(programaService);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando programa social informado não existir")
    void deveLancarNaoEncontradoExceptionQuandoProgramaSocialNaoExistir() {
        // Arrange
        BeneficiarioCreateRequest dto = new BeneficiarioCreateRequest(10L, 99L);
        PedidoAjuda pedidoAprovado = criarPedido(10L, StatusPedido.APROVADO);
        when(pedidoAjudaService.buscarEntidadePorId(10L)).thenReturn(pedidoAprovado);
        when(programaService.buscarPorId(99L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.criarBeneficiario(dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Programa social não encontrado.");

        verify(repository, never()).criar(any());
    }

    @Test
    @DisplayName("Deve buscar beneficiário por CPF com sucesso")
    void deveBuscarBeneficiarioPorCpfComSucesso() {
        // Arrange
        String cpf = CPF_VALIDO;
        Beneficiario beneficiario = criarBeneficiario(1L);
        when(repository.buscarPorCpf(cpf)).thenReturn(beneficiario);

        // Act
        BeneficiarioResponse resultado = beneficiarioService.buscarPorCpf(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.cpf()).isEqualTo(cpf);
        assertThat(resultado.nomeCompleto()).isEqualTo("Maria Joana da Silva");
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao buscar por CPF quando formato for inválido")
    void deveLancarValidacaoExceptionAoBuscarPorCpfInvalido() {
        // Arrange
        String cpfInvalido = "123";

        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.buscarPorCpf(cpfInvalido))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("CPF inválido.");

        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao buscar por CPF inexistente")
    void deveLancarNaoEncontradoExceptionAoBuscarPorCpfInexistente() {
        // Arrange
        String cpfInexistente = "52998224725";
        when(repository.buscarPorCpf(cpfInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.buscarPorCpf(cpfInexistente))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Beneficiário não encontrado.");
    }

    @Test
    @DisplayName("Deve buscar beneficiário por ID com sucesso")
    void deveBuscarBeneficiarioPorIdComSucesso() {
        // Arrange
        long id = 5L;
        Beneficiario beneficiario = criarBeneficiario(id);
        when(repository.buscarPorId(id)).thenReturn(beneficiario);

        // Act
        BeneficiarioResponse resultado = beneficiarioService.buscarPorId(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao buscar por ID inexistente")
    void deveLancarNaoEncontradoExceptionAoBuscarPorIdInexistente() {
        // Arrange
        long idInexistente = 999L;
        when(repository.buscarPorId(idInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.buscarPorId(idInexistente))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Beneficiário não encontrado.");
    }

    @Test
    @DisplayName("Deve listar todos os beneficiários mapeados")
    void deveListarTodosOsBeneficiarios() {
        // Arrange
        Beneficiario b1 = criarBeneficiario(1L);
        Beneficiario b2 = criarBeneficiario(2L);
        when(repository.listarTodos()).thenReturn(List.of(b1, b2));

        // Act
        List<BeneficiarioResponse> lista = beneficiarioService.listarTodos();

        // Assert
        assertThat(lista).hasSize(2);
    }

    @Test
    @DisplayName("Deve listar beneficiários por cidade")
    void deveListarBeneficiariosPorCidade() {
        // Arrange
        Beneficiario b = criarBeneficiario(1L);
        when(repository.listarPorCidade("São Paulo")).thenReturn(List.of(b));

        // Act
        List<BeneficiarioResponse> lista = beneficiarioService.listarPorCidade("São Paulo");

        // Assert
        assertThat(lista).hasSize(1);
    }

    @Test
    @DisplayName("Deve listar beneficiários por programa social")
    void deveListarBeneficiariosPorPrograma() {
        // Arrange
        Beneficiario b = criarBeneficiario(1L);
        when(repository.listarPorPrograma(1L)).thenReturn(List.of(b));

        // Act
        List<BeneficiarioResponse> lista = beneficiarioService.listarPorPrograma(1L);

        // Assert
        assertThat(lista).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar dados do beneficiário e delegar atualização de endereço")
    void deveAtualizarBeneficiarioComSucesso() {
        // Arrange
        String cpf = CPF_VALIDO;
        EnderecoRequest novoEnderecoDTO = new EnderecoRequest("01001000", "200");
        BeneficiarioUpdateRequest request = new BeneficiarioUpdateRequest("11911112222", "novo@email.com", novoEnderecoDTO);
        Beneficiario beneficiario = criarBeneficiario(1L);

        when(repository.atualizar(cpf, request)).thenReturn(beneficiario);

        // Act
        BeneficiarioResponse resultado = beneficiarioService.atualizar(cpf, request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(beneficiario.getTelefone()).isEqualTo("11911112222");
        assertThat(beneficiario.getEmail()).isEqualTo("novo@email.com");
        verify(enderecoService, times(1)).entradaEndereco(
                eq(beneficiario.getEndereco()),
                eq(novoEnderecoDTO),
                eq(TipoEndereco.RESIDENCIAL)
        );
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao atualizar beneficiário com CPF inexistente")
    void deveLancarNaoEncontradoExceptionAoAtualizarBeneficiarioInexistente() {
        // Arrange
        String cpf = "00000000000";
        BeneficiarioUpdateRequest request = new BeneficiarioUpdateRequest("11911112222", "novo@email.com", null);
        when(repository.atualizar(cpf, request)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.atualizar(cpf, request))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Beneficiário não encontrado, CPF inválido.");

        verifyNoInteractions(enderecoService);
    }

    @Test
    @DisplayName("Deve excluir beneficiário com sucesso quando CPF for válido e registro existir")
    void deveExcluirBeneficiarioComSucesso() {
        // Arrange
        String cpfValido = CPF_VALIDO;
        when(repository.excluir(cpfValido)).thenReturn(1L);

        // Act
        boolean resultado = beneficiarioService.excluir(cpfValido);

        // Assert
        assertThat(resultado).isTrue();
        verify(repository, times(1)).excluir(cpfValido);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao tentar excluir com CPF inválido")
    void deveLancarNaoEncontradoExceptionAoExcluirComCpfInvalido() {
        // Arrange
        String cpfInvalido = "123";

        // Act & Assert
        assertThatThrownBy(() -> beneficiarioService.excluir(cpfInvalido))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("CPF inválido.");

        verifyNoInteractions(repository);
    }
}
