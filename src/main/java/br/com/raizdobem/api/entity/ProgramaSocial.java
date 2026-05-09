package br.com.raizdobem.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "programa_social")
public class ProgramaSocial {
    @Id
    @Column(name = "id_programa")
    private Long id;
    private String programa;
}
