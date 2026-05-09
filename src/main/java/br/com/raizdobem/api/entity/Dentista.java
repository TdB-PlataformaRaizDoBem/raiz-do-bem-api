package br.com.raizdobem.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Dentista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    @Column(name = "id_dentista")
    private Long id;

    @JsonProperty("croDentista")
    @Column(name = "cro")
    private String croDentista;

    @Column(name = "cpf")
    private String cpf;

    @JsonProperty("nomeCompleto")
    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo")
    private Sexo sexo;

    @Column(name = "email")
    private String email;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "disponivel")
    private String disponivel;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

}
