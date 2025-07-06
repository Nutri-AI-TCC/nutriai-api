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
    private long id;

    @Column(name = "ARQUIVO_DIETA")
    private String linkArquivo;

    @Column(name = "NOME_DOCUMENTO_DIETA")
    private String nomeArquivo;

    @Column(name = "ID_PACIENTE_DIETA")
    private long idPaciente;

    @Column(name = "ATIVO_DIETA")
    private int ativo_dieta;


}
