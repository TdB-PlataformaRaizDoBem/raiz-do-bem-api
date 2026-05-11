package br.com.raizdobem.api.service;

import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.dto.response.EnderecoDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AtendimentoMatchService {
    @Inject
    DentistaService dentistaService;

//    @Inject
//    GoogleMapsService googleMapsService;

//    public DentistaDTO melhorMatchDentista(BeneficiarioDTO beneficiarioDTO){
//        List<DentistaDTO> dentistas = dentistaService.listarTodos();
//
//        String enderecoBeneficiario = montarEndereco(beneficiarioDTO.getEndereco());
//
////        List<String> enderecosDentistas = dentistas.stream()
////                .map(d ->)
////                .toList();
//
//        for(DentistaDTO dentista : dentistas){
//            Integer distancia =
//                    googleMapsService.calcularDistancia(
//                            enderecoBeneficiario,
//                            enderecoDentista
//                    );
//        }
//        return melhorDentista;
//    }

    public String montarEndereco(EnderecoDTO dto){
        String endereco;
        if(dto.numero() == null){
            endereco = dto.logradouro() + ", " + dto.cidade() + ", " + dto.estado();
            return endereco;
        }
        endereco = dto.logradouro() + ", " + dto.numero() + ", " + dto.cidade() + ", " + dto.estado();
        return endereco;
    }
}
