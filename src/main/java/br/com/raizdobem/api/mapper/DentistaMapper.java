package br.com.raizdobem.api.mapper;

import br.com.raizdobem.api.dto.response.DentistaResponse;
import br.com.raizdobem.api.entity.Dentista;
import br.com.raizdobem.api.entity.Especialidade;
import br.com.raizdobem.api.entity.ProgramaSocial;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class DentistaMapper {
    public static DentistaResponse mapeamentoParaResponse(Dentista dentista){
        if (dentista == null) {
            return null;
        }

        return new DentistaResponse(
                dentista.getId(),
                dentista.getCroDentista(),
                dentista.getCpf(),
                dentista.getNomeCompleto(),
                dentista.getSexo() != null ? dentista.getSexo().name() : "N/A",
                dentista.getEmail(),
                dentista.getTelefone(),
                dentista.getCategoria(),
                dentista.getEspecialidades() != null ? dentista.getEspecialidades().stream()
                        .map(Especialidade::getDescricao)
                        .collect(Collectors.toList()) : new ArrayList<>(),
                dentista.getProgramasSociais() != null ? dentista.getProgramasSociais().stream()
                        .map(ProgramaSocial::getPrograma)
                        .collect(Collectors.toList()) : new ArrayList<>(),
                dentista.getDisponivel(),
                dentista.getEndereco() != null ? dentista.getEndereco().getLogradouro() : null,
                dentista.getEndereco() != null ? dentista.getEndereco().getNumero() : null,
                dentista.getEndereco() != null ? dentista.getEndereco().getCidade() : null,
                dentista.getEndereco() != null ? dentista.getEndereco().getEstado() : null,
                dentista.getEndereco() != null ? dentista.getEndereco().getCep() : null
        );
    }

    public static List<DentistaResponse> mapeamentoParaResponse(List<Dentista> dentistas){
        if (dentistas == null) {
            return null;
        }

        return dentistas.stream().map(DentistaMapper::mapeamentoParaResponse).collect(Collectors.toList());
    }

//    public static Dentista mapeamentoParaEntidade(DentistaCreateRequest request){
//        return new Dentista(request.croDentista(), request.cpf(), request.nomeCompleto(), request.sexo().toString(),
//                            request.email(), request.telefone(), request.categoria(),
//                            request.disponivel(), request.endereco());
//    }
}
