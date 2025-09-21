package com.nutriai.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DIETAS", schema = "ADMIN")
@NoArgsConstructor
@AllArgsConstructor
public class Dieta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dieta_seq_gen")
    @SequenceGenerator(name = "dieta_seq_gen", sequenceName = "DIETA_SEQ", allocationSize = 1)
    @Column(name = "ID_DIETA")
    private Long id;

    @Column(name = "NOME_DOCUMENTO_DIETA", nullable = false)
    private String nomeDocumento;

    @Column(name = "ARQUIVO_DIETA", nullable = false)
    private String arquivoUrl;

    @Column(name = "ATIVO_DIETA", columnDefinition = "NUMBER(1)")
    private boolean ativo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE_DIETA", nullable = false)
    private Paciente paciente;

    public String getNomeDocumento() {
        return nomeDocumento;
    }

    public void setNomeDocumento(String nomeDocumento) {
        this.nomeDocumento = nomeDocumento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArquivoUrl() {
        return arquivoUrl;
    }

    public void setArquivoUrl(String arquivoUrl) {
        this.arquivoUrl = arquivoUrl;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}
