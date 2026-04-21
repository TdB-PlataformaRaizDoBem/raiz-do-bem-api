package br.com.raizdobem.api.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Beneficiario {
    @Id
    @Column(name = "id_beneficiario")
    private Long id;
    private String cpf;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    private String telefone;
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_pedido_ajuda")
    private PedidoAjuda pedido;

    @ManyToOne
    @JoinColumn(name = "id_programa_social")
    private ProgramaSocial programaSocial;

    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    public Beneficiario() {
    }

    public Beneficiario(Long id, String cpf, String nomeCompleto, LocalDate dataNascimento, String telefone, String email, PedidoAjuda pedido, ProgramaSocial programaSocial, Endereco endereco) {
        this.id = id;
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.pedido = pedido;
        this.programaSocial = programaSocial;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PedidoAjuda getPedido() {
        return pedido;
    }

    public void setPedido(PedidoAjuda pedido) {
        this.pedido = pedido;
    }

    public ProgramaSocial getProgramaSocial() {
        return programaSocial;
    }

    public void setProgramaSocial(ProgramaSocial programaSocial) {
        this.programaSocial = programaSocial;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
