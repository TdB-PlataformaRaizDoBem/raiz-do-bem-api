package br.com.raizdobem.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "pedido_ajuda")
public class PedidoAjuda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    @JsonProperty("cpf")
    private String cpf;

    @JsonProperty("nomeCompleto")
    @Column(name = "nome_completo")
    private String nomeCompleto;

    @JsonProperty("dataNascimento")
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private String telefone;
    private String email;

    @JsonProperty("descricaoProblema")
    @Column(name = "descricao_problema")
    private String descricaoProblema;

    @JsonProperty("dataPedido")
    @Column(name = "data_pedido")
    private LocalDate dataPedido;

    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    @Column(name = "status_pedido")
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @ManyToOne
    @JoinColumn(name = "id_dentista")
    private Dentista dentista;
}
