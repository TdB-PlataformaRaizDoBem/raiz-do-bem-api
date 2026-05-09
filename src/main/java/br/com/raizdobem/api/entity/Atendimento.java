package br.com.raizdobem.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Atendimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atendimento")
    private Long id;
    private String prontuario;

    @Column(name = "data_inicial")
    private LocalDate dataInicial;
    @Column(name = "data_final")
    private LocalDate dataFinal;

    @ManyToOne
    @JoinColumn(name = "id_beneficiario")
    private Beneficiario beneficiario;

    @ManyToOne
    @JoinColumn(name = "id_dentista")
    private Dentista dentista;

    @ManyToOne
    @JoinColumn(name = "id_colaborador")
    private Colaborador colaborador;

}
