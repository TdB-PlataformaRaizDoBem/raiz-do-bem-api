package br.com.raizdobem.api.model;

public class ProgramaSocial {
    private Long id;
    private String programa;

    public ProgramaSocial() {
    }

    public ProgramaSocial(Long id, String programa) {
        this.id = id;
        this.programa = programa;
    }

    public Long getId() {
        return id;
    }

    public ProgramaSocial setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPrograma() {
        return programa;
    }

    public ProgramaSocial setPrograma(String programa) {
        this.programa = programa;
        return this;
    }
}
