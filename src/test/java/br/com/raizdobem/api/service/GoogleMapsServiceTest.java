package br.com.raizdobem.api.service;

import br.com.raizdobem.api.client.GoogleMapsClient;
import br.com.raizdobem.api.dto.external.GoogleMapsRequestDTO;
import br.com.raizdobem.api.dto.external.GoogleMapsResponseDTO;
import br.com.raizdobem.api.dto.response.DentistaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - GoogleMapsService")
class GoogleMapsServiceTest {

    @Mock
    private GoogleMapsClient googleMapsClient;

    @InjectMocks
    private GoogleMapsService googleMapsService;

    @BeforeEach
    void setUp() {
        googleMapsService.chaveApi = "chave-teste-api-google";
    }

    private DentistaResponse criarDentistaDTO(Long id, String nome, String logradouro, String numero) {
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
                logradouro,
                numero,
                "São Paulo",
                "SP",
                "01001000"
        );
    }

    @Test
    @DisplayName("Deve retornar o dentista com menor distância calculada pela API do Google Maps")
    void deveRetornarDentistaComMenorDistancia() {
        // Arrange
        String enderecoBeneficiario = "Praça da Sé, 100, São Paulo, SP";
        DentistaResponse dentistaLonge = criarDentistaDTO(1L, "Dr. Longe", "Av Paulista", "2000");
        DentistaResponse dentistaPerto = criarDentistaDTO(2L, "Dra. Perto", "Rua Boa Vista", "50");
        List<DentistaResponse> dentistas = List.of(dentistaLonge, dentistaPerto);

        GoogleMapsResponseDTO rotaDentista0 = new GoogleMapsResponseDTO(0, 0, 8500, "ROUTE_EXISTS");
        GoogleMapsResponseDTO rotaDentista1 = new GoogleMapsResponseDTO(0, 1, 1200, "ROUTE_EXISTS");

        when(googleMapsClient.buscarRotas(
                eq("chave-teste-api-google"),
                eq("originIndex,destinationIndex,distanceMeters,condition"),
                any(GoogleMapsRequestDTO.class)
        )).thenReturn(List.of(rotaDentista0, rotaDentista1));

        // Act
        DentistaResponse resultado = googleMapsService.calcularDistanciaProximidade(enderecoBeneficiario, dentistas);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(2L);
        assertThat(resultado.nomeCompleto()).isEqualTo("Dra. Perto");
    }

    @Test
    @DisplayName("Deve retornar o primeiro dentista como fallback quando ocorrer exceção na chamada da API externa")
    void deveRetornarPrimeiroDentistaComoFallbackQuandoOcorrerExcecao() {
        // Arrange
        String enderecoBeneficiario = "Praça da Sé, 100, São Paulo, SP";
        DentistaResponse dentista1 = criarDentistaDTO(1L, "Dr. Primeiro", "Av Paulista", "1000");
        DentistaResponse dentista2 = criarDentistaDTO(2L, "Dra. Segunda", "Av Brasil", "500");
        List<DentistaResponse> dentistas = List.of(dentista1, dentista2);

        when(googleMapsClient.buscarRotas(any(), any(), any()))
                .thenThrow(new RuntimeException("Google Maps API offline / Timeout"));

        // Act
        DentistaResponse resultado = googleMapsService.calcularDistanciaProximidade(enderecoBeneficiario, dentistas);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nomeCompleto()).isEqualTo("Dr. Primeiro");
    }

    @Test
    @DisplayName("Deve selecionar a primeira rota quando nenhuma rota possuir status ROUTE_EXISTS")
    void deveSelecionarPrimeiraRotaQuandoNaoHouverRouteExists() {
        // Arrange
        String enderecoBeneficiario = "Praça da Sé, 100, São Paulo, SP";
        DentistaResponse d1 = criarDentistaDTO(1L, "Dr. Um", "Rua A", "10");
        DentistaResponse d2 = criarDentistaDTO(2L, "Dra. Dois", "Rua B", "20");
        List<DentistaResponse> dentistas = List.of(d1, d2);

        GoogleMapsResponseDTO rotaSemCaminho = new GoogleMapsResponseDTO(0, 0, 0, "ROUTE_NOT_FOUND");

        when(googleMapsClient.buscarRotas(any(), any(), any()))
                .thenReturn(List.of(rotaSemCaminho));

        // Act
        DentistaResponse resultado = googleMapsService.calcularDistanciaProximidade(enderecoBeneficiario, dentistas);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
    }
}
