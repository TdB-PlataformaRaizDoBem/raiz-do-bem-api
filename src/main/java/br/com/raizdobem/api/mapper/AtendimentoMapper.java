package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.AtendimentoDTO;
import br.com.raizdobem.api.entity.Atendimento;

import java.util.List;

public class AtendimentoMapper {
    public static AtendimentoDTO mapeamentoAtendimento(Atendimento atendimento){
        if(atendimento == null){
            return null;
        }

        return new AtendimentoDTO(
                atendimento.getId(),
                atendimento.getProntuario(),
                atendimento.getBeneficiario().getNomeCompleto(),
                atendimento.getDentista().getNomeCompleto(),
                atendimento.getDataInicial(),
                atendimento.getDataFinal() != null ? atendimento.getDataFinal().toString() : "NÃO FINALIZADO"
        );
    }

    public static List<AtendimentoDTO> mapeamentoAtendimentos(List<Atendimento> atendimentos){
        if(atendimentos == null)
            return null;

        return atendimentos.stream()
                .map(AtendimentoMapper :: mapeamentoAtendimento)
                .toList();
    }
}
