package br.com.raizdobem.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Dentista {
    @Id
    @GeneratedValue
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
    private String categoria;

    private boolean disponivel;
    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

}
