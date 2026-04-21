package br.com.raizdobem.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Colaborador {
    @Id
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

    public Colaborador() {
    }

    public Colaborador(Long id, String cpf, String nomeCompleto, LocalDate dataNascimento, LocalDate dataContratacao, String email) {
        this.id = id;
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.dataContratacao = dataContratacao;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public Colaborador setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCpf() {
        return cpf;
    }

    public Colaborador setCpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public Colaborador setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
        return this;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Colaborador setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public LocalDate getDataContratacao() {
        return dataContratacao;
    }

    public Colaborador setDataContratacao(LocalDate dataContratacao) {
        this.dataContratacao = dataContratacao;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Colaborador setEmail(String email) {
        this.email = email;
        return this;
    }
}
