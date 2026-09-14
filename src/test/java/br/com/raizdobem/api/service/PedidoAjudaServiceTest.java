package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.PedidoAjudaUpdateRequest;
import br.com.raizdobem.api.dto.request.PedidoAjudaCreateRequest;
import br.com.raizdobem.api.dto.request.EnderecoRequest;
import br.com.raizdobem.api.dto.response.PedidoAjudaDTO;
import br.com.raizdobem.api.entity.*;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RegraNegocioException;
import br.com.raizdobem.api.exception.ValidacaoException;
import br.com.raizdobem.api.repository.PedidoAjudaRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - PedidoAjudaService")
class PedidoAjudaServiceTest {

    private static final String CPF_VALIDO = "52998224725";

    @Mock
    private PedidoAjudaRepository repository;

    @Mock
    private EnderecoService enderecoService;

    @Mock
    private DentistaService dentistaService;

    @InjectMocks
    private PedidoAjudaService pedidoAjudaService;

    private EnderecoRequest criarEntradaEnderecoDTO() {
        return new EnderecoRequest("01001000", "100");
    }

    private Endereco criarEnderecoEntidade() {
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

    private PedidoAjudaCreateRequest criarPedidoDTO(String sexo, LocalDate dataNascimento) {
        return new PedidoAjudaCreateRequest(
                CPF_VALIDO,
                "Carlos Souza",
                dataNascimento,
                sexo,
                "11999990000",
                "carlos@email.com",
                "Dor de dente crônica",
                criarEntradaEnderecoDTO()
        );
    }

    private PedidoAjuda criarPedidoEntidade(Long id, StatusPedido status) {
        PedidoAjuda pedido = new PedidoAjuda();
        pedido.setId(id);
        pedido.setCpf(CPF_VALIDO);
        pedido.setNomeCompleto("Carlos Souza");
        pedido.setDataNascimento(LocalDate.of(2010, 1, 1));
        pedido.setSexo(Sexo.M);
        pedido.setStatus(status);
        pedido.setTelefone("11999990000");
        pedido.setEmail("carlos@email.com");
        pedido.setDescricaoProblema("Tratamento de canal");
        pedido.setDataPedido(LocalDate.now());
        pedido.setEndereco(criarEnderecoEntidade());
        return pedido;
    }

    private Dentista criarDentistaEntidade(Long id, String categoria) {
        Dentista dentista = new Dentista();
        dentista.setId(id);
        dentista.setNomeCompleto("Dra. Paula Fernandes");
        dentista.setCategoria(categoria);
        return dentista;
    }

    @Test
    @DisplayName("Deve criar pedido com status PENDENTE quando for do sexo feminino")
    void deveCriarPedidoComStatusPendenteQuandoSexoFeminino() {
        // Arrange
        LocalDate dataNascimento = LocalDate.now().minusYears(25);
        PedidoAjudaCreateRequest dto = criarPedidoDTO("F", dataNascimento);
        Endereco endereco = criarEnderecoEntidade();
        when(enderecoService.criarComoSuporte(dto.endereco(), TipoEndereco.RESIDENCIAL)).thenReturn(endereco);

        // Act
        PedidoAjuda resultado = pedidoAjudaService.criar(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatus()).isEqualTo(StatusPedido.PENDENTE);
        assertThat(resultado.getSexo()).isEqualTo(Sexo.F);
        assertThat(resultado.getEndereco()).isEqualTo(endereco);
        verify(repository, times(1)).criar(any(PedidoAjuda.class));
    }

    @Test
    @DisplayName("Deve criar pedido com status PENDENTE quando sexo for masculino e menor de idade")
    void deveCriarPedidoComStatusPendenteQuandoSexoMasculinoMenorDeIdade() {
        // Arrange
        LocalDate dataNascimento = LocalDate.now().minusYears(15);
        PedidoAjudaCreateRequest dto = criarPedidoDTO("M", dataNascimento);
        Endereco endereco = criarEnderecoEntidade();
        when(enderecoService.criarComoSuporte(dto.endereco(), TipoEndereco.RESIDENCIAL)).thenReturn(endereco);

        // Act
        PedidoAjuda resultado = pedidoAjudaService.criar(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatus()).isEqualTo(StatusPedido.PENDENTE);
        assertThat(resultado.getSexo()).isEqualTo(Sexo.M);
        verify(repository, times(1)).criar(any(PedidoAjuda.class));
    }

    @Test
    @DisplayName("Deve criar pedido com status REJEITADO automaticamente quando sexo for masculino e maior de idade")
    void deveCriarPedidoComStatusRejeitadoQuandoSexoMasculinoEMaiorDeIdade() {
        // Arrange
        LocalDate dataNascimento = LocalDate.now().minusYears(20);
        PedidoAjudaCreateRequest dto = criarPedidoDTO("M", dataNascimento);
        Endereco endereco = criarEnderecoEntidade();
        when(enderecoService.criarComoSuporte(dto.endereco(), TipoEndereco.RESIDENCIAL)).thenReturn(endereco);

        // Act
        PedidoAjuda resultado = pedidoAjudaService.criar(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatus()).isEqualTo(StatusPedido.REJEITADO);
        verify(repository, times(1)).criar(any(PedidoAjuda.class));
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao criar pedido com CPF inválido")
    void deveLancarValidacaoExceptionAoCriarPedidoComCpfInvalido() {
        // Arrange
        PedidoAjudaCreateRequest dto = new PedidoAjudaCreateRequest(
                "11111111111",
                "Carlos Souza",
                LocalDate.now().minusYears(20),
                "F",
                "11999990000",
                "carlos@email.com",
                "Dor de dente",
                criarEntradaEnderecoDTO()
        );

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.criar(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Cpf inválido");

        verifyNoInteractions(enderecoService);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao criar pedido quando campo sexo for nulo")
    void deveLancarValidacaoExceptionAoCriarPedidoQuandoSexoForNulo() {
        // Arrange
        PedidoAjudaCreateRequest dto = criarPedidoDTO(null, LocalDate.of(2000, 1, 1));

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.criar(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Sexo é obrigatório.");

        verifyNoInteractions(enderecoService);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao criar pedido quando endereço retornado for nulo")
    void deveLancarNaoEncontradoExceptionAoCriarPedidoQuandoEnderecoNaoForEncontrado() {
        // Arrange
        PedidoAjudaCreateRequest dto = criarPedidoDTO("F", LocalDate.now().minusYears(10));
        when(enderecoService.criarComoSuporte(dto.endereco(), TipoEndereco.RESIDENCIAL)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.criar(dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Endereço não encontrado.");

        verify(repository, never()).criar(any());
    }

    @Test
    @DisplayName("Deve processar pedido com sucesso para APROVADO quando aprovador for Dentista Coordenador")
    void deveProcessarPedidoComSucessoQuandoAprovadoPorDentistaCoordenador() {
        // Arrange
        long pedidoId = 10L;
        long dentistaId = 5L;
        PedidoAjudaUpdateRequest dto = new PedidoAjudaUpdateRequest(StatusPedido.APROVADO, dentistaId);
        PedidoAjuda pedido = criarPedidoEntidade(pedidoId, StatusPedido.PENDENTE);
        Dentista coordenador = criarDentistaEntidade(dentistaId, "COORDENADOR");

        when(repository.findById(pedidoId)).thenReturn(pedido);
        when(dentistaService.buscarEntidadePorId(dentistaId)).thenReturn(coordenador);

        // Act
        PedidoAjudaDTO resultado = pedidoAjudaService.processarPedido(pedidoId, dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.status()).isEqualTo(StatusPedido.APROVADO);
        assertThat(resultado.dentistaResponsavel()).isEqualTo("Dra. Paula Fernandes");
        assertThat(pedido.getDentista()).isEqualTo(coordenador);
    }

    @Test
    @DisplayName("Deve processar pedido com sucesso para REJEITADO sem exigir dentista aprovador")
    void deveProcessarPedidoComSucessoQuandoNovoStatusForRejeitado() {
        // Arrange
        long pedidoId = 11L;
        PedidoAjudaUpdateRequest dto = new PedidoAjudaUpdateRequest(StatusPedido.REJEITADO, 0L);
        PedidoAjuda pedido = criarPedidoEntidade(pedidoId, StatusPedido.PENDENTE);

        when(repository.findById(pedidoId)).thenReturn(pedido);

        // Act
        PedidoAjudaDTO resultado = pedidoAjudaService.processarPedido(pedidoId, dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.status()).isEqualTo(StatusPedido.REJEITADO);
        verifyNoInteractions(dentistaService);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar processar um pedido que já está REJEITADO")
    void deveLancarRegraNegocioExceptionAoProcessarPedidoQuandoPedidoJaEstiverRejeitado() {
        // Arrange
        long pedidoId = 12L;
        PedidoAjudaUpdateRequest dto = new PedidoAjudaUpdateRequest(StatusPedido.APROVADO, 5L);
        PedidoAjuda pedidoRejeitado = criarPedidoEntidade(pedidoId, StatusPedido.REJEITADO);

        when(repository.findById(pedidoId)).thenReturn(pedidoRejeitado);

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.processarPedido(pedidoId, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Pedido REJEITADO não pode ser processado.");

        verifyNoInteractions(dentistaService);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException ao processar pedido com status PENDENTE ou nulo")
    void deveLancarValidacaoExceptionAoProcessarPedidoQuandoStatusForInvalido() {
        // Arrange
        long pedidoId = 13L;
        PedidoAjudaUpdateRequest dtoPendente = new PedidoAjudaUpdateRequest(StatusPedido.PENDENTE, 5L);
        PedidoAjudaUpdateRequest dtoNulo = new PedidoAjudaUpdateRequest(null, 5L);
        PedidoAjuda pedido = criarPedidoEntidade(pedidoId, StatusPedido.PENDENTE);

        when(repository.findById(pedidoId)).thenReturn(pedido);

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.processarPedido(pedidoId, dtoPendente))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Pedido só pode ser atualizado para APROVADO/REJEITADO.");

        assertThatThrownBy(() -> pedidoAjudaService.processarPedido(pedidoId, dtoNulo))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Pedido só pode ser atualizado para APROVADO/REJEITADO.");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao processar quando o pedido não existir")
    void deveLancarNaoEncontradoExceptionAoProcessarPedidoInexistente() {
        // Arrange
        long pedidoIdInexistente = 99L;
        PedidoAjudaUpdateRequest dto = new PedidoAjudaUpdateRequest(StatusPedido.APROVADO, 5L);
        when(repository.findById(pedidoIdInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.processarPedido(pedidoIdInexistente, dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Pedido de ajuda não encontrado.");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando dentista aprovador não for encontrado")
    void deveLancarNaoEncontradoExceptionQuandoDentistaAprovadorNaoExistir() {
        // Arrange
        long pedidoId = 14L;
        long dentistaIdInexistente = 88L;
        PedidoAjudaUpdateRequest dto = new PedidoAjudaUpdateRequest(StatusPedido.APROVADO, dentistaIdInexistente);
        PedidoAjuda pedido = criarPedidoEntidade(pedidoId, StatusPedido.PENDENTE);

        when(repository.findById(pedidoId)).thenReturn(pedido);
        when(dentistaService.buscarEntidadePorId(dentistaIdInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.processarPedido(pedidoId, dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Dentista aprovador não encontrado.");
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando dentista aprovador não possuir categoria COORDENADOR")
    void deveLancarRegraNegocioExceptionQuandoDentistaNaoForCoordenador() {
        // Arrange
        long pedidoId = 15L;
        long dentistaId = 7L;
        PedidoAjudaUpdateRequest dto = new PedidoAjudaUpdateRequest(StatusPedido.APROVADO, dentistaId);
        PedidoAjuda pedido = criarPedidoEntidade(pedidoId, StatusPedido.PENDENTE);
        Dentista voluntario = criarDentistaEntidade(dentistaId, "VOLUNTARIO");

        when(repository.findById(pedidoId)).thenReturn(pedido);
        when(dentistaService.buscarEntidadePorId(dentistaId)).thenReturn(voluntario);

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.processarPedido(pedidoId, dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Apenas um Dentista Coordenador pode aprovar um pedido de ajuda.");
    }

    @Test
    @DisplayName("Deve buscar pedido por CPF com sucesso")
    void deveBuscarPedidoPorCpfComSucesso() {
        // Arrange
        String cpf = CPF_VALIDO;
        PedidoAjuda pedido = criarPedidoEntidade(1L, StatusPedido.PENDENTE);
        when(repository.buscarPorCpf(cpf)).thenReturn(pedido);

        // Act
        PedidoAjudaDTO resultado = pedidoAjudaService.buscarPorCpf(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.cpf()).isEqualTo(cpf);
        assertThat(resultado.nomeCompleto()).isEqualTo("Carlos Souza");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao buscar por CPF inexistente")
    void deveLancarNaoEncontradoExceptionAoBuscarPorCpfInexistente() {
        // Arrange
        String cpf = "00000000000";
        when(repository.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> pedidoAjudaService.buscarPorCpf(cpf))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Pedido de ajuda não encontrado.");
    }

    @Test
    @DisplayName("Deve listar todos os pedidos de ajuda mapeados para DTO")
    void deveListarTodosOsPedidos() {
        // Arrange
        PedidoAjuda p1 = criarPedidoEntidade(1L, StatusPedido.PENDENTE);
        PedidoAjuda p2 = criarPedidoEntidade(2L, StatusPedido.APROVADO);
        when(repository.listarTodos()).thenReturn(List.of(p1, p2));

        // Act
        List<PedidoAjudaDTO> resultado = pedidoAjudaService.listarTodos();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).id()).isEqualTo(1L);
        assertThat(resultado.get(1).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Deve listar pedidos por data corretamente")
    void deveListarPedidosPorData() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        PedidoAjuda p1 = criarPedidoEntidade(1L, StatusPedido.PENDENTE);
        when(repository.listarPorData(hoje)).thenReturn(List.of(p1));

        // Act
        List<PedidoAjudaDTO> resultado = pedidoAjudaService.listarPorData(hoje);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve excluir pedido e retornar booleano do repositório")
    void deveExcluirPedidoComSucesso() {
        // Arrange
        long id = 10L;
        when(repository.excluir(id)).thenReturn(true);

        // Act
        boolean resultado = pedidoAjudaService.excluir(id);

        // Assert
        assertThat(resultado).isTrue();
        verify(repository, times(1)).excluir(id);
    }

    @Test
    @DisplayName("Deve calcular maioridade corretamente via método estático")
    void deveCalcularMaioridadeCorretamente() {
        // Arrange
        LocalDate menorIdade = LocalDate.now().minusYears(17).minusMonths(11);
        LocalDate exatamente18 = LocalDate.now().minusYears(18);
        LocalDate maiorIdade = LocalDate.now().minusYears(30);

        // Act & Assert
        assertThat(PedidoAjudaService.maiorIdade(null)).isFalse();
        assertThat(PedidoAjudaService.maiorIdade(menorIdade)).isFalse();
        assertThat(PedidoAjudaService.maiorIdade(exatamente18)).isTrue();
        assertThat(PedidoAjudaService.maiorIdade(maiorIdade)).isTrue();
    }
}
