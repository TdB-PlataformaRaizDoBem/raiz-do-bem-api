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
@Table(name = "beneficiario")
public class BeneficiarioDTO {
    @Id
    @GeneratedValue
    @Column(name = "id_beneficiario")
    private Long id;
    private String cpf;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    private String telefone;
    private String email;

    @ManyToOne
    @JoinColumn(name = "id_pedido_ajuda")
    private PedidoAjudaDTO pedido;

    @ManyToOne
    @JoinColumn(name = "id_programa_social")
    private ProgramaSocialDTO programaSocialDTO;

    @ManyToOne
    @JoinColumn(name = "id_endereco")
    private EnderecoDTO enderecoDTO;

}
