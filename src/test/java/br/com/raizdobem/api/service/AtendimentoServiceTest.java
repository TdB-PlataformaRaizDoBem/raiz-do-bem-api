package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.request.AtendimentoUpdateRequest;
import br.com.raizdobem.api.dto.request.AtendimentoCreateRequest;
import br.com.raizdobem.api.dto.response.AtendimentoResponse;
import br.com.raizdobem.api.dto.response.DentistaResponse;
import br.com.raizdobem.api.entity.Atendimento;
import br.com.raizdobem.api.entity.Beneficiario;
import br.com.raizdobem.api.entity.Colaborador;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Endereco;
import br.com.raizdobem.api.entity.TipoEndereco;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.repository.AtendimentoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AtendimentoService")
class AtendimentoServiceTest {

    @Mock
    private AtendimentoRepository repository;

    @Mock
    private DentistaService dentistaService;

    @Mock
    private ColaboradorService colaboradorService;

    @Mock
    private BeneficiarioRepository beneficiarioRepository;

    @Mock
    private AtendimentoMatchService atendimentoMatchService;

    @InjectMocks
    private AtendimentoService atendimentoService;

    private Endereco criarEndereco() {
        Endereco e = new Endereco();
        e.setId(1L);
        e.setTipoEndereco(TipoEndereco.RESIDENCIAL);
        e.setCep("01001000");
        e.setLogradouro("Praça da Sé");
        e.setNumero("100");
        e.setBairro("Sé");
        e.setCidade("São Paulo");
        e.setEstado("SP");
        return e;
    }

    private Beneficiario criarBeneficiario() {
        Beneficiario b = new Beneficiario();
        b.setId(1L);
        b.setCpf("12345678901");
        b.setNomeCompleto("Joãozinho Silva");
        b.setDataNascimento(LocalDate.of(2015, 1, 1));
        b.setTelefone("11988887777");
        b.setEmail("joao@email.com");
        b.setEndereco(criarEndereco());
        return b;
    }

    private Dentista criarDentista() {
        Dentista d = new Dentista();
        d.setId(10L);
        d.setNomeCompleto("Dra. Beatriz Santos");
        d.setCpf("98765432100");
        d.setCroDentista("123456");
        d.setEmail("beatriz@odontoclinic.com");
        d.setTelefone("11977776666");
        d.setCategoria("VOLUNTARIO");
        d.setDisponivel("true");
        d.setEndereco(criarEndereco());
        return d;
    }

    private DentistaResponse criarDentistaDTO() {
        return new DentistaResponse(
                10L,
                "123456",
                "98765432100",
                "Dra. Beatriz Santos",
                "F",
                "beatriz@odontoclinic.com",
                "11977776666",
                "VOLUNTARIO",
                List.of(),
                List.of(),
                "true",
                "Praça da Sé",
                "100",
                "São Paulo",
                "SP",
                "01001000"
        );
    }

    private Colaborador criarColaborador() {
        Colaborador c = new Colaborador();
        c.setId(3L);
        c.setNomeCompleto("Roberto Gerente");
        c.setCpf("11122233344");
        return c;
    }

    @Test
    @DisplayName("Deve criar atendimento com sucesso associando beneficiário e melhor match de dentista")
    void deveCriarAtendimentoComSucessoQuandoDadosForemValidos() {
        // Arrange
        String cpf = "12345678901";
        AtendimentoCreateRequest dto = new AtendimentoCreateRequest("PRONT-001", cpf, LocalDate.now());
        Beneficiario beneficiario = criarBeneficiario();
        Dentista dentista = criarDentista();
        DentistaResponse dentistaResponse = criarDentistaDTO();

        when(beneficiarioRepository.buscarPorCpf(cpf)).thenReturn(beneficiario);
        when(atendimentoMatchService.melhorMatchDentista(any())).thenReturn(dentistaResponse);
        when(dentistaService.buscarEntidadePorId(dentistaResponse.id())).thenReturn(dentista);

        // Act
        AtendimentoResponse resultado = atendimentoService.criarAtendimento(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.prontuario()).isEqualTo("PRONT-001");
        assertThat(resultado.beneficiario()).isEqualTo("Joãozinho Silva");
        assertThat(resultado.dentista()).isEqualTo("Dra. Beatriz Santos");

        verify(repository, times(1)).criar(any(Atendimento.class));
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao criar atendimento quando beneficiário não for encontrado")
    void deveLancarNaoEncontradoExceptionAoCriarAtendimentoQuandoBeneficiarioNaoExistir() {
        // Arrange
        String cpf = "00000000000";
        AtendimentoCreateRequest dto = new AtendimentoCreateRequest("PRONT-001", cpf, LocalDate.now());
        when(beneficiarioRepository.buscarPorCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> atendimentoService.criarAtendimento(dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Beneficiário não foi encontrado.");

        verifyNoInteractions(atendimentoMatchService);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao criar atendimento quando entidade do dentista não for encontrada")
    void deveLancarNaoEncontradoExceptionAoCriarAtendimentoQuandoDentistaNaoExistir() {
        // Arrange
        String cpf = "12345678901";
        AtendimentoCreateRequest dto = new AtendimentoCreateRequest("PRONT-001", cpf, LocalDate.now());
        Beneficiario beneficiario = criarBeneficiario();
        DentistaResponse dentistaResponse = criarDentistaDTO();

        when(beneficiarioRepository.buscarPorCpf(cpf)).thenReturn(beneficiario);
        when(atendimentoMatchService.melhorMatchDentista(any())).thenReturn(dentistaResponse);
        when(dentistaService.buscarEntidadePorId(dentistaResponse.id())).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> atendimentoService.criarAtendimento(dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Dentista não foi encontrado.");

        verify(repository, never()).criar(any());
    }

    @Test
    @DisplayName("Deve buscar atendimento por CPF com sucesso")
    void deveBuscarAtendimentoPorCpfComSucesso() {
        // Arrange
        String cpf = "12345678901";
        Atendimento atendimento = new Atendimento();
        atendimento.setId(1L);
        atendimento.setProntuario("PRONT-123");
        atendimento.setBeneficiario(criarBeneficiario());
        atendimento.setDentista(criarDentista());
        atendimento.setDataInicial(LocalDate.now());

        when(repository.buscarPeloCpf(cpf)).thenReturn(atendimento);

        // Act
        AtendimentoResponse resultado = atendimentoService.buscarPorCpf(cpf);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.prontuario()).isEqualTo("PRONT-123");
    }

    @Test
    @DisplayName("Deve listar todos os atendimentos cadastrados")
    void deveListarAtendimentosCadastrados() {
        // Arrange
        Atendimento a = new Atendimento();
        a.setId(1L);
        a.setProntuario("PRONT-1");
        a.setBeneficiario(criarBeneficiario());
        a.setDentista(criarDentista());
        a.setDataInicial(LocalDate.now());

        when(repository.listarTodos()).thenReturn(List.of(a));

        // Act
        List<AtendimentoResponse> lista = atendimentoService.listarAtendimentos();

        // Assert
        assertThat(lista).hasSize(1);
    }

    @Test
    @DisplayName("Deve encerrar atendimento com sucesso vinculando colaborador e data de finalização")
    void deveEncerrarAtendimentoComSucessoQuandoDadosForemValidos() {
        // Arrange
        String cpf = "12345678901";
        AtendimentoUpdateRequest dto = new AtendimentoUpdateRequest("PRONT-FINAL", 3L);
        Atendimento atendimento = new Atendimento();
        atendimento.setId(1L);
        atendimento.setBeneficiario(criarBeneficiario());
        Colaborador colaborador = criarColaborador();

        when(repository.buscarPeloCpf(cpf)).thenReturn(atendimento);
        when(colaboradorService.buscarPorId(3L)).thenReturn(colaborador);

        // Act
        atendimentoService.encerrarAtendimento(cpf, dto);

        // Assert
        assertThat(atendimento.getProntuario()).isEqualTo("PRONT-FINAL");
        assertThat(atendimento.getColaborador()).isEqualTo(colaborador);
        assertThat(atendimento.getDataFinal()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao encerrar atendimento inexistente")
    void deveLancarNaoEncontradoExceptionAoEncerrarAtendimentoInexistente() {
        // Arrange
        String cpf = "00000000000";
        AtendimentoUpdateRequest dto = new AtendimentoUpdateRequest("PRONT", 3L);
        when(repository.buscarPeloCpf(cpf)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> atendimentoService.encerrarAtendimento(cpf, dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Atendimento não encontrado");

        verifyNoInteractions(colaboradorService);
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao encerrar atendimento com prontuário nulo")
    void deveLancarNaoEncontradoExceptionAoEncerrarComProntuarioNulo() {
        // Arrange
        String cpf = "12345678901";
        AtendimentoUpdateRequest dto = new AtendimentoUpdateRequest(null, 3L);
        Atendimento atendimento = new Atendimento();
        when(repository.buscarPeloCpf(cpf)).thenReturn(atendimento);

        // Act & Assert
        assertThatThrownBy(() -> atendimentoService.encerrarAtendimento(cpf, dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Prontuário inválido, não foi possível atualizar atendimento.");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao encerrar atendimento com ID de colaborador nulo")
    void deveLancarNaoEncontradoExceptionAoEncerrarComIdColaboradorNulo() {
        // Arrange
        String cpf = "12345678901";
        AtendimentoUpdateRequest dto = new AtendimentoUpdateRequest("PRONT", null);
        Atendimento atendimento = new Atendimento();
        when(repository.buscarPeloCpf(cpf)).thenReturn(atendimento);

        // Act & Assert
        assertThatThrownBy(() -> atendimentoService.encerrarAtendimento(cpf, dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Id de colaborador inválido.");
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException ao encerrar atendimento quando colaborador não existir")
    void deveLancarNaoEncontradoExceptionAoEncerrarQuandoColaboradorNaoExistir() {
        // Arrange
        String cpf = "12345678901";
        AtendimentoUpdateRequest dto = new AtendimentoUpdateRequest("PRONT", 99L);
        Atendimento atendimento = new Atendimento();
        when(repository.buscarPeloCpf(cpf)).thenReturn(atendimento);
        when(colaboradorService.buscarPorId(99L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> atendimentoService.encerrarAtendimento(cpf, dto))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Colaborador inválido, não foi possível atualizar atendimento.");
    }

    @Test
    @DisplayName("Deve excluir atendimento por ID")
    void deveExcluirAtendimentoPorId() {
        // Arrange
        long id = 1L;
        when(repository.excluir(id)).thenReturn(true);

        // Act
        boolean resultado = atendimentoService.excluir(id);

        // Assert
        assertThat(resultado).isTrue();
        verify(repository, times(1)).excluir(id);
    }
}
