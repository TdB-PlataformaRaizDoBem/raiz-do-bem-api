package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.dto.response.EnderecoDTO;
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

    public DentistaDTO melhorMatchDentista(BeneficiarioDTO beneficiarioDTO){
        List<DentistaDTO> dentistas = dentistaService.listarDisponiveis();

        if(dentistas.isEmpty())
            throw new NaoEncontradoException("Nenhum dentista disponível para vincular ao atendimento.");

        String enderecoBeneficiario = montarEndereco(beneficiarioDTO.getEndereco());

        return googleMapsService.calcularDistanciaProximidade(enderecoBeneficiario, dentistas);
    }

    public static String montarEndereco(EnderecoDTO dto){
        if (dto == null)
            throw new ValidacaoException("Endereço do beneficiário é obrigatório para calcular a proximidade com os dentistas.");

        if(dto.numero() == null)
            return dto.logradouro() + ", " + dto.cidade() + ", " + dto.estado();
        return dto.logradouro() + ", " + dto.numero() + ", " + dto.cidade() + ", " + dto.estado();
    }
}
