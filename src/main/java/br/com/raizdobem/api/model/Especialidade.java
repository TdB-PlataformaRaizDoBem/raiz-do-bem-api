package br.com.raizdobem.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Especialidade {
    @Id
    @Column(name = "id_especialidade")
    private Long id;

    private String descricao;

    public Long getId() {
        return id;
    }

    public Especialidade setId(Long id) {
        this.id = id;
        return this;
    }

    public String getDescricao() {
        return descricao;
    }

    public Especialidade setDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public Especialidade() {
    }
}
