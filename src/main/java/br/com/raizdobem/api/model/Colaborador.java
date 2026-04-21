package br.com.raizdobem.api.model;

import java.time.LocalDate;

public class Colaborador {
    private int id;
    private String cpf;
    private String nomeCompleto;
    private LocalDate dataNascimento;
    private LocalDate dataContratacao;
    private String email;

    public Colaborador() {
    }

    public Colaborador(int id, String cpf, String nomeCompleto, LocalDate dataNascimento, LocalDate dataContratacao, String email) {
        this.id = id;
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.dataContratacao = dataContratacao;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public Colaborador setId(int id) {
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
