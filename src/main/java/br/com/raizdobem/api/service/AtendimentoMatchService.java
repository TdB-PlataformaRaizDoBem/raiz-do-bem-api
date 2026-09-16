package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.response.BeneficiarioResponse;
import br.com.raizdobem.api.dto.response.DentistaResponse;
import br.com.raizdobem.api.dto.response.EnderecoResponse;
import br.com.raizdobem.api.exception.NaoEncontradoException;
import br.com.raizdobem.api.exception.ValidacaoException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AtendimentoMatchService {
    @Inject
    DentistaService dentistaService;

    @Inject
    GoogleMapsService googleMapsService;

    public DentistaResponse melhorMatchDentista(BeneficiarioResponse beneficiarioResponse){
        List<DentistaResponse> dentistas = dentistaService.listarDisponiveis();

        if(dentistas.isEmpty())
            throw new NaoEncontradoException("Nenhum dentista disponível para vincular ao atendimento.");

        String enderecoBeneficiario = montarEndereco(beneficiarioResponse.endereco());
        return googleMapsService.calcularDistanciaProximidade(enderecoBeneficiario, dentistas);
    }

    public static String montarEndereco(EnderecoResponse dto){
        if (dto == null)
            throw new ValidacaoException("Endereço do beneficiário é obrigatório para calcular a proximidade com os dentistas.");

        if(dto.numero() == null)
            return dto.logradouro() + ", " + dto.cidade() + ", " + dto.estado();
        return dto.logradouro() + ", " + dto.numero() + ", " + dto.cidade() + ", " + dto.estado();
    }
}
