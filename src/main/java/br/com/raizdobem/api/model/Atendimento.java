package br.com.raizdobem.api.model;

import java.time.LocalDate;

//@Entity
public class Atendimento {
//    @Id
//    @GeneratedValue
    private Long id;
    private String prontuario;
    private LocalDate dataInicial;
    private LocalDate dataFinal;
//    @ManyToOne
    private Beneficiario beneficiario;
//    @ManyToOne
    private Dentista dentista;
//    @ManyToOne
    private Colaborador colaborador;

    public Atendimento() {
    }
    public Atendimento(Long id, String prontuario, LocalDate dataInicial, LocalDate dataFinal, Beneficiario beneficiario, Dentista dentista, Colaborador colaborador) {
        this.id = id;
        this.prontuario = prontuario;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.beneficiario = beneficiario;
        this.dentista = dentista;
        this.colaborador = colaborador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProntuario() {
        return prontuario;
    }

    public void setProntuario(String prontuario) {
        this.prontuario = prontuario;
    }

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(LocalDate dataInicial) {
        this.dataInicial = dataInicial;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Beneficiario getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(Beneficiario beneficiario) {
        this.beneficiario = beneficiario;
    }

    public Dentista getDentista() {
        return dentista;
    }

    public void setDentista(Dentista dentista) {
        this.dentista = dentista;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }
}
