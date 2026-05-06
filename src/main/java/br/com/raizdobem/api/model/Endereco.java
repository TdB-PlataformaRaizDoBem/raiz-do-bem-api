package br.com.raizdobem.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Long id;
    @JsonProperty("logradouro")
    private String logradouro;

    @JsonProperty("cep")
    private String cep;

    @JsonProperty("numero")
    private String numero;

    @JsonProperty("bairro")
    private String bairro;
    @JsonProperty("cidade")
    private String cidade;

    @JsonProperty("estado")
    private String estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_endereco")
    private TipoEndereco tipoEndereco;

}
