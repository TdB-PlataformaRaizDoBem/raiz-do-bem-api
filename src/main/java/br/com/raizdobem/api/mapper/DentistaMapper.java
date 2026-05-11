package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.DentistaDTO;
import br.com.raizdobem.api.entity.Dentista;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DentistaMapper {
    public static DentistaDTO mapeamentoDTO(Dentista dentista){
        if (dentista == null) {
            return null;
        }

        return new DentistaDTO(
                dentista.getId(),
                dentista.getCroDentista(),
                dentista.getCpf(),
                dentista.getNomeCompleto(),
                dentista.getSexo() != null ? dentista.getSexo().name() : "N/A",
                dentista.getEmail(),
                dentista.getTelefone(),
                dentista.getCategoria(),
                dentista.getEspecialidades() != null ? dentista.getEspecialidades().stream()
                        .map(e -> e.getDescricao())
                        .collect(Collectors.toList()) : new ArrayList<>(),
                dentista.getProgramasSociais() != null ? dentista.getProgramasSociais().stream()
                        .map(p -> p.getPrograma())
                        .collect(Collectors.toList()) : new ArrayList<>(),
                dentista.getDisponivel(),
                dentista.getEndereco() != null ? dentista.getEndereco().getLogradouro() : null,
                dentista.getEndereco() != null ? dentista.getEndereco().getNumero() : null,
                dentista.getEndereco() != null ? dentista.getEndereco().getCidade() : null,
                dentista.getEndereco() != null ? dentista.getEndereco().getEstado() : null
        );
    }

    public static List<DentistaDTO> mapeamentoListaDTO(List<Dentista> dentistas){
        if (dentistas == null) {
            return null;
        }

        return dentistas.stream()
                .map(DentistaMapper :: mapeamentoDTO)
                .collect(Collectors.toList());
    }
}
