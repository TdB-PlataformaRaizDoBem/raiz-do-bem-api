package br.com.raizdobem.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco")
public class Endereco {

    //    @GeneratedValue
    @Id
    @Column(name = "id_endereco")
    public Long id;
    public String logradouro;
    public String cep;
    public String numero;
    public String bairro;
    public String cidade;
    public String estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_endereco")
    TipoEndereco tipoEndereco;

    public Endereco() {
    }

}
