package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.BeneficiarioDTO;
import br.com.raizdobem.api.dto.response.BeneficiarioResumidoDTO;
import br.com.raizdobem.api.dto.response.EnderecoDTO;
import br.com.raizdobem.api.dto.response.PedidoAjudaResumidoDTO;
import br.com.raizdobem.api.entity.Beneficiario;

import java.util.List;

public class BeneficiarioMapper {
    public static BeneficiarioDTO mapeamentoDTO(Beneficiario beneficiario) {
        if (beneficiario == null) {
            return null;
        }

        return new BeneficiarioDTO(
                beneficiario.getId(),
                beneficiario.getCpf(),
                beneficiario.getNomeCompleto(),
                beneficiario.getDataNascimento(),
                beneficiario.getTelefone(),
                beneficiario.getEmail(),
                beneficiario.getPedido() != null ? new PedidoAjudaResumidoDTO(
                        beneficiario.getPedido().getId(),
                        beneficiario.getPedido().getDentista() != null ? beneficiario.getPedido().getDentista().getNomeCompleto() : null
                ) : null,
                beneficiario.getProgramaSocial() != null ? beneficiario.getProgramaSocial().getPrograma() : "N/A",
                beneficiario.getEndereco() != null ? new EnderecoDTO(
                        beneficiario.getEndereco().getId(),
                        beneficiario.getEndereco().getLogradouro(),
                        beneficiario.getEndereco().getCep(),
                        beneficiario.getEndereco().getNumero(),
                        beneficiario.getEndereco().getBairro(),
                        beneficiario.getEndereco().getCidade(),
                        beneficiario.getEndereco().getEstado(),
                        beneficiario.getEndereco().getTipoEndereco() != null ? beneficiario.getEndereco().getTipoEndereco().name() : null
                ) : null
        );
    }

    public static List<BeneficiarioDTO> mapeamentoListaDTO(List<Beneficiario> beneficiarios){
        if(beneficiarios == null)
            return null;

        return beneficiarios.stream()
                .map(BeneficiarioMapper :: mapeamentoDTO)
                .toList();
    }
}
