package br.com.raizdobem.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "atendimento")
public class AtendimentoDTO {
    @Id
    @GeneratedValue
    @Column(name = "id_atendimento")
    private Long id;
    private String prontuario;

    @Column(name = "data_inicial")
    private LocalDate dataInicial;
    @Column(name = "data_final")
    private LocalDate dataFinal;

    @ManyToOne
    @JoinColumn(name = "id_beneficiario")
    private BeneficiarioDTO beneficiarioDTO;

    @ManyToOne
    @JoinColumn(name = "id_dentista")
    private DentistaDTO dentistaDTO;

    @ManyToOne
    @JoinColumn(name = "id_colaborador")
    private ColaboradorDTO colaboradorDTO;

}
