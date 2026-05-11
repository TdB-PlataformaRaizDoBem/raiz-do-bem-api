package br.com.raizdobem.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Beneficiario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beneficiario")
    private Long id;

    @Pattern(regexp = "\\d{11}",
            message = "CPF deve conter 11 números")
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
}
