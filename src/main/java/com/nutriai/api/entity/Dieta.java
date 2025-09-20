package com.nutriai.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DIETAS", schema = "ADMIN")
@Data
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

    @Column(name = "ARQUIVO_DIETA", nullable = false) // Armazenará a URL do arquivo
    private String arquivoUrl;

    @Column(name = "ATIVO_DIETA", columnDefinition = "NUMBER(1)")
    private boolean ativo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE_DIETA", nullable = false)
    private Paciente paciente;

}
