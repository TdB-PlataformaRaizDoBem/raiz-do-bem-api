package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.response.BeneficiarioResponse;
import br.com.raizdobem.api.dto.response.DentistaResponse;
import br.com.raizdobem.api.dto.response.EnderecoResponse;
import br.com.raizdobem.api.dto.response.PedidoAjudaResumidoDTO;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AtendimentoMatchService")
class AtendimentoMatchServiceTest {

    @Mock
    private DentistaService dentistaService;

    @Mock
    private GoogleMapsService googleMapsService;

    @InjectMocks
    private AtendimentoMatchService atendimentoMatchService;

    private EnderecoResponse criarEnderecoDTO(String numero) {
        return new EnderecoResponse(
                1L,
                "Praça da Sé",
                "01001000",
                numero,
                "Sé",
                "São Paulo",
                "SP",
                "RESIDENCIAL"
        );
    }

    private BeneficiarioResponse criarBeneficiarioDTO(EnderecoResponse endereco) {
        return new BeneficiarioResponse(
                10L,
                "12345678901",
                "Joãozinho Silva",
                LocalDate.of(2015, 5, 20),
                "11988887777",
                "joao@email.com",
                new PedidoAjudaResumidoDTO(1L, "Dra. Ana"),
                "Dentista do Bem",
                endereco
        );
    }

    private DentistaResponse criarDentistaDTO(Long id, String nome) {
        return new DentistaResponse(
                id,
                "123456",
                "98765432100",
                nome,
                "M",
                "dentista@email.com",
                "11999998888",
                "VOLUNTARIO",
                List.of(),
                List.of(),
                "true",
                "Avenida Paulista",
                "1000",
                "São Paulo",
                "SP",
                "01310100"
        );
    }

    @Test
    @DisplayName("Deve retornar o dentista mais próximo quando houver dentistas disponíveis e endereço completo")
    void deveRetornarMelhorMatchQuandoHouverDentistasDisponiveis() {
        // Arrange
        EnderecoResponse endereco = criarEnderecoDTO("100");
        BeneficiarioResponse beneficiario = criarBeneficiarioDTO(endereco);
        DentistaResponse dentistaEsperado = criarDentistaDTO(1L, "Dra. Ana Costa");
        List<DentistaResponse> dentistasDisponiveis = List.of(dentistaEsperado);

        when(dentistaService.listarDisponiveis()).thenReturn(dentistasDisponiveis);
        when(googleMapsService.calcularDistanciaProximidade(
                eq("Praça da Sé, 100, São Paulo, SP"),
                eq(dentistasDisponiveis)
        )).thenReturn(dentistaEsperado);

        // Act
        DentistaResponse resultado = atendimentoMatchService.melhorMatchDentista(beneficiario);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeCompleto()).isEqualTo("Dra. Ana Costa");

        verify(dentistaService, times(1)).listarDisponiveis();
        verify(googleMapsService, times(1)).calcularDistanciaProximidade(
                "Praça da Sé, 100, São Paulo, SP",
                dentistasDisponiveis
        );
    }

    @Test
    @DisplayName("Deve formatar endereço sem número quando o número for nulo")
    void deveFormatarEnderecoSemNumeroQuandoNumeroForNulo() {
        // Arrange
        EnderecoResponse enderecoSemNumero = criarEnderecoDTO(null);
        BeneficiarioResponse beneficiario = criarBeneficiarioDTO(enderecoSemNumero);
        DentistaResponse dentista = criarDentistaDTO(2L, "Dr. Carlos Eduardo");
        List<DentistaResponse> dentistas = List.of(dentista);

        when(dentistaService.listarDisponiveis()).thenReturn(dentistas);
        when(googleMapsService.calcularDistanciaProximidade(
                eq("Praça da Sé, São Paulo, SP"),
                eq(dentistas)
        )).thenReturn(dentista);

        // Act
        DentistaResponse resultado = atendimentoMatchService.melhorMatchDentista(beneficiario);

        // Assert
        assertThat(resultado).isNotNull();
        verify(googleMapsService, times(1)).calcularDistanciaProximidade(
                "Praça da Sé, São Paulo, SP",
                dentistas
        );
    }

    @Test
    @DisplayName("Deve lançar NaoEncontradoException quando não houver nenhum dentista disponível")
    void deveLancarNaoEncontradoExceptionQuandoNaoHouverDentistasDisponiveis() {
        // Arrange
        BeneficiarioResponse beneficiario = criarBeneficiarioDTO(criarEnderecoDTO("50"));
        when(dentistaService.listarDisponiveis()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> atendimentoMatchService.melhorMatchDentista(beneficiario))
                .isInstanceOf(NaoEncontradoException.class)
                .hasMessage("Nenhum dentista disponível para vincular ao atendimento.");

        verify(dentistaService, times(1)).listarDisponiveis();
        verifyNoInteractions(googleMapsService);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando o endereço do beneficiário for nulo")
    void deveLancarValidacaoExceptionQuandoEnderecoDoBeneficiarioForNulo() {
        // Arrange
        BeneficiarioResponse beneficiarioSemEndereco = criarBeneficiarioDTO(null);
        List<DentistaResponse> dentistas = List.of(criarDentistaDTO(3L, "Dr. Roberto"));
        when(dentistaService.listarDisponiveis()).thenReturn(dentistas);

        // Act & Assert
        assertThatThrownBy(() -> atendimentoMatchService.melhorMatchDentista(beneficiarioSemEndereco))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Endereço do beneficiário é obrigatório para calcular a proximidade com os dentistas.");

        verify(dentistaService, times(1)).listarDisponiveis();
        verifyNoInteractions(googleMapsService);
    }

    @Test
    @DisplayName("Deve montar endereço corretamente com o método estático quando dados forem válidos")
    void deveMontarEnderecoCorretamenteViaMetodoEstatico() {
        // Arrange
        EnderecoResponse comNumero = criarEnderecoDTO("42");
        EnderecoResponse semNumero = criarEnderecoDTO(null);

        // Act & Assert
        assertThat(AtendimentoMatchService.montarEndereco(comNumero))
                .isEqualTo("Praça da Sé, 42, São Paulo, SP");
        assertThat(AtendimentoMatchService.montarEndereco(semNumero))
                .isEqualTo("Praça da Sé, São Paulo, SP");
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException no método estático montarEndereco quando DTO for nulo")
    void deveLancarValidacaoExceptionAoMontarEnderecoComDtoNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AtendimentoMatchService.montarEndereco(null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Endereço do beneficiário é obrigatório para calcular a proximidade com os dentistas.");
    }
}
