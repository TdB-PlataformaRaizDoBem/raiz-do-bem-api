package br.com.raizdobem.api.model;

import jakarta.persistence.*;

@Entity
public class Dentista {
    @Id
    @Column(name = "id_dentista")
    private Long id;

    @Column(name = "cro")
    private String croDentista;

    private String cpf;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private String email;
    private String telefone;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    private boolean disponivel;
    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    public Dentista() {
    }

    public Dentista(Long id, String croDentista, String cpf, String nomeCompleto, Sexo sexo, String email, String telefone, Categoria categoria, boolean disponivel, Endereco endereco) {
        this.id = id;
        this.croDentista = croDentista;
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.sexo = sexo;
        this.email = email;
        this.telefone = telefone;
        this.categoria = categoria;
        this.disponivel = disponivel;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCroDentista() {
        return croDentista;
    }

    public void setCroDentista(String croDentista) {
        this.croDentista = croDentista;
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

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
