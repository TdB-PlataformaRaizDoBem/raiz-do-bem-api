package br.com.raizdobem.api.service;

import br.com.raizdobem.api.client.ViaCepClient;
import br.com.raizdobem.api.dto.external.ViaCepDTO;
import br.com.raizdobem.api.dto.request.EntradaEnderecoCompletoDTO;
import br.com.raizdobem.api.dto.request.EnderecoRequest;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.TipoEndereco;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.RegraNegocioException;
import br.com.raizdobem.api.exception.RequisicaoInvalidaException;
import br.com.raizdobem.api.repository.EnderecoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - EnderecoService")
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository repository;

    @Mock
    private ViaCepClient client;

    @InjectMocks
    private EnderecoService enderecoService;

    private ViaCepDTO criarViaCepSucesso(String cep) {
        return new ViaCepDTO(
                cep,
                "Avenida Brigadeiro Faria Lima",
                "Pinheiros",
                "São Paulo",
                "SP",
                "São Paulo",
                null
        );
    }

    @Test
    @DisplayName("Deve criar endereço completo com sucesso consultando ViaCEP e persistindo")
    void deveCriarEnderecoCompletoComSucesso() {
        // Arrange
        String cep = "01452002";
        EntradaEnderecoCompletoDTO dto = new EntradaEnderecoCompletoDTO(cep, "1000", "PROFISSIONAL");
        ViaCepDTO viaCep = criarViaCepSucesso(cep);

        when(client.buscarEndereco(cep)).thenReturn(viaCep);

        // Act
        Endereco resultado = enderecoService.criar(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCep()).isEqualTo(cep);
        assertThat(resultado.getLogradouro()).isEqualTo("Avenida Brigadeiro Faria Lima");
        assertThat(resultado.getNumero()).isEqualTo("1000");
        assertThat(resultado.getBairro()).isEqualTo("Pinheiros");
        assertThat(resultado.getCidade()).isEqualTo("São Paulo");
        assertThat(resultado.getEstado()).isEqualTo("SP");
        assertThat(resultado.getTipoEndereco()).isEqualTo(TipoEndereco.PROFISSIONAL);

        verify(repository, times(1)).criar(any(Endereco.class));
    }

    @Test
    @DisplayName("Deve criar endereço como suporte com sucesso")
    void deveCriarEnderecoComoSuporteComSucesso() {
        // Arrange
        String cep = "01001000";
        EnderecoRequest dto = new EnderecoRequest(cep, "50");
        ViaCepDTO viaCep = new ViaCepDTO(cep, "Praça da Sé", "Sé", "São Paulo", "SP", "São Paulo", null);

        when(client.buscarEndereco(cep)).thenReturn(viaCep);

        // Act
        Endereco resultado = enderecoService.criarComoSuporte(dto, TipoEndereco.RESIDENCIAL);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCep()).isEqualTo(cep);
        assertThat(resultado.getTipoEndereco()).isEqualTo(TipoEndereco.RESIDENCIAL);
        assertThat(resultado.getLogradouro()).isEqualTo("Praça da Sé");
        verify(repository, times(1)).criar(any(Endereco.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "123456789", "01452-002", "abcdefgh", ""})
    @DisplayName("Deve lançar RegraNegocioException ao criar com CEP em formato inválido")
    void deveLancarRegraNegocioExceptionQuandoCepForInvalidoAoCriar(String cepInvalido) {
        // Arrange
        EntradaEnderecoCompletoDTO dto = new EntradaEnderecoCompletoDTO(cepInvalido, "100", "RESIDENCIAL");

        // Act & Assert
        assertThatThrownBy(() -> enderecoService.criar(dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("CEP inválido! Insira 8 dígitos!");

        verifyNoInteractions(client);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar RequisicaoInvalidaException quando ViaCEP retornar resposta nula")
    void deveLancarRequisicaoInvalidaExceptionQuandoViaCepRetornarNulo() {
        // Arrange
        String cep = "01001000";
        EntradaEnderecoCompletoDTO dto = new EntradaEnderecoCompletoDTO(cep, "100", "RESIDENCIAL");
        when(client.buscarEndereco(cep)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> enderecoService.criar(dto))
                .isInstanceOf(RequisicaoInvalidaException.class)
                .hasMessage("Requisição ViaCep inválida.");

        verify(repository, never()).criar(any());
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando ViaCEP retornar erro de CEP inexistente")
    void deveLancarNaoEncontradoExceptionQuandoViaCepRetornarErro() {
        // Arrange
        String cepInexistente = "99999999";
        EntradaEnderecoCompletoDTO dto = new EntradaEnderecoCompletoDTO(cepInexistente, "100", "RESIDENCIAL");
        ViaCepDTO viaCepErro = new ViaCepDTO(null, null, null, null, null, null, "true");

        when(client.buscarEndereco(cepInexistente)).thenReturn(viaCepErro);

        // Act & Assert
        assertThatThrownBy(() -> enderecoService.criar(dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Endereço Inválido. CEP não encontrado");

        verify(repository, never()).criar(any());
    }

    @Test
    @DisplayName("Deve atualizar endereço com sucesso quando ID existir")
    void deveAtualizarEnderecoComSucesso() {
        // Arrange
        long id = 1L;
        String cep = "01452002";
        EntradaEnderecoCompletoDTO dto = new EntradaEnderecoCompletoDTO(cep, "200", "RESIDENCIAL");
        Endereco enderecoExistente = new Endereco();
        enderecoExistente.setId(id);
        ViaCepDTO viaCep = criarViaCepSucesso(cep);

        when(repository.buscarPeloId(id)).thenReturn(enderecoExistente);
        when(client.buscarEndereco(cep)).thenReturn(viaCep);

        // Act
        Endereco atualizado = enderecoService.atualizarEndereco(id, dto);

        // Assert
        assertThat(atualizado).isNotNull();
        assertThat(atualizado.getNumero()).isEqualTo("200");
        assertThat(atualizado.getLogradouro()).isEqualTo("Avenida Brigadeiro Faria Lima");
        verify(repository, times(1)).persist(enderecoExistente);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao atualizar endereço inexistente")
    void deveLancarNaoEncontradoExceptionAoAtualizarEnderecoInexistente() {
        // Arrange
        long idInexistente = 999L;
        EntradaEnderecoCompletoDTO dto = new EntradaEnderecoCompletoDTO("01452002", "200", "RESIDENCIAL");
        when(repository.buscarPeloId(idInexistente)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> enderecoService.atualizarEndereco(idInexistente, dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Endereço não encontrado.");

        verifyNoInteractions(client);
    }

    @Test
    @DisplayName("Deve buscar endereço por ID")
    void deveBuscarEnderecoPorId() {
        // Arrange
        long id = 5L;
        Endereco endereco = new Endereco();
        endereco.setId(id);
        when(repository.buscarPeloId(id)).thenReturn(endereco);

        // Act
        Endereco resultado = enderecoService.buscaPorId(id);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve listar todos os endereços")
    void deveListarTodosOsEnderecos() {
        // Arrange
        when(repository.listarTodos()).thenReturn(List.of(new Endereco(), new Endereco()));

        // Act
        List<Endereco> lista = enderecoService.listarTodos();

        // Assert
        assertThat(lista).hasSize(2);
    }

    @Test
    @DisplayName("Deve listar endereços por cidade")
    void deveListarEnderecosPorCidade() {
        // Arrange
        when(repository.listarPorCidade("Campinas")).thenReturn(List.of(new Endereco()));

        // Act
        List<Endereco> lista = enderecoService.listarPorCidades("Campinas");

        // Assert
        assertThat(lista).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar endereço diretamente no cliente ViaCEP")
    void deveBuscarEnderecoNoViaCepClient() {
        // Arrange
        String cep = "01001000";
        ViaCepDTO viaCep = criarViaCepSucesso(cep);
        when(client.buscarEndereco(cep)).thenReturn(viaCep);

        // Act
        ViaCepDTO resultado = enderecoService.buscarEndereco(cep);

        // Assert
        assertThat(resultado).isEqualTo(viaCep);
    }

    @Test
    @DisplayName("Deve excluir endereço com sucesso")
    void deveExcluirEnderecoComSucesso() {
        // Arrange
        long id = 7L;
        when(repository.excluir(id)).thenReturn(true);

        // Act
        boolean resultado = enderecoService.excluir(id);

        // Assert
        assertThat(resultado).isTrue();
        verify(repository, times(1)).excluir(id);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "1234567", "123456789", "01001-000", "0100100A", "        "})
    @DisplayName("Deve validar formato de CEP estático retornando false para formatos inválidos")
    void deveValidarCepEstaticoInvalido(String cepInvalido) {
        assertThat(EnderecoService.validarCep(cepInvalido)).isFalse();
    }

    @Test
    @DisplayName("Deve validar formato de CEP estático retornando true para exatamente 8 dígitos numéricos")
    void deveValidarCepEstaticoValido() {
        assertThat(EnderecoService.validarCep("01001000")).isTrue();
    }
}
