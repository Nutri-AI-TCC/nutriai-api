package com.nutriai.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PACIENTES", schema = "ADMIN")
@Data
public class Paciente {

    @Id
    @SequenceGenerator(name = "paciente_seq_gen", sequenceName = "PACIENTE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paciente_seq_gen")
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOME_PACIENTE", nullable = false)
    private String nome;

    @Column(name = "NASCIMENTO_PACIENTE")
    private LocalDate nascimento;

    @Column(name = "PESO_PACIENTE", precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(name = "ALTURA_PACIENTE", precision = 3, scale = 2)
    private BigDecimal altura;

    @Column(name = "CNPJ_CPF_PACIENTE")
    private String cnpjCpf;

    @Lob
    @Column(name = "ALERGIAS_PACIENTE")
    private String alergias;

    @Lob
    @Column(name = "COMORBIDADES_PACIENTE")
    private String comorbidades;

    @Lob
    @Column(name = "MEDICACOES_PACIENTE")
    private String medicacoes;

    @Column(name = "ATIVO_PACIENTE", columnDefinition = "NUMBER(1)")
    private boolean ativo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_PACIENTE", nullable = false)
    private Usuario usuario;
}