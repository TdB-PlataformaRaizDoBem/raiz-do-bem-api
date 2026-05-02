package br.com.raizdobem.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "pedido_ajuda")
public class PedidoAjudaDTO {
    @Id
    @GeneratedValue
    @Column(name = "id_pedido")
    private Long id;
    private String cpf;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    private String telefone;
    private String email;
    @Column(name = "descricao_problema")
    private String descricaoProblema;

    @Column(name = "data_pedido")
    private LocalDate dataPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido")
    private StatusPedido status;
    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private EnderecoDTO enderecoDTO;

    @ManyToOne
    @JoinColumn(name = "id_dentista")
    private DentistaDTO dentistaDTO;
}
