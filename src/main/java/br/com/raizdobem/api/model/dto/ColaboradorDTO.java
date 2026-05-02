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
@Table(name = "colaborador")
public class ColaboradorDTO {
    @Id
    @GeneratedValue
    @Column(name = "id_colaborador")
    private Long id;

    private String cpf;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_contratacao")
    private LocalDate dataContratacao;
    private String email;
}
