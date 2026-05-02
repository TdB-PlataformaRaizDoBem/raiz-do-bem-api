package br.com.raizdobem.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "endereco")
public class EnderecoDTO {

    @Id
    @GeneratedValue
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

}
