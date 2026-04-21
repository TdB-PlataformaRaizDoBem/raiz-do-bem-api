package br.com.raizdobem.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "programa_social")
public class ProgramaSocial {
    @Id
    @Column(name = "id_programa")
    private Long id;
    private String programa;

    public ProgramaSocial() {
    }

    public ProgramaSocial(Long id, String programa) {
        this.id = id;
        this.programa = programa;
    }

    public Long getId() {
        return id;
    }

    public ProgramaSocial setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPrograma() {
        return programa;
    }

    public ProgramaSocial setPrograma(String programa) {
        this.programa = programa;
        return this;
    }
}
