package br.com.raizdobem.api.model;

import jakarta.persistence.*;

@Entity
public class Endereco {

    @GeneratedValue
    @Id
    @Column(name = "id_endereco")
    private Long id;
    private String logradouro;
    private String cep;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_endereco")
    private TipoEndereco tipoEndereco;

    public Endereco() {
    }

    public Long getId() {
        return id;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getCep() {
        return cep;
    }

    public Endereco setCep(String cep) {
        this.cep = cep;
        return this;
    }

    public String getNumero() {
        return numero;
    }

    public Endereco setNumero(String numero) {
        this.numero = numero;
        return this;
    }

    public String getBairro() {
        return bairro;
    }


    public String getCidade() {
        return cidade;
    }

    public Endereco setCidade(String cidade) {
        this.cidade = cidade;
        return this;
    }

    public String getEstado() {
        return estado;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public Endereco setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
        return this;
    }
}
