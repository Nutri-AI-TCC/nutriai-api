package com.nutriai.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma única mensagem no histórico de um chat.
 */
@Entity
@Table(name = "HISTORICO", schema = "ADMIN")
@Data
@NoArgsConstructor
public class Historico {

    @Id
    @SequenceGenerator(name = "historico_seq_gen", sequenceName = "HISTORICO_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "historico_seq_gen")
    @Column(name = "ID_HISTORICO")
    private Long id;

    @Lob
    @Column(name = "DS_MENSAGENS_CHAT", nullable = false)
    private String conteudo;

    @Column(name = "DT_CRIACAO_CHAT", nullable = false)
    private LocalDateTime dataEnvio;

    @Enumerated(EnumType.STRING) // Armazena o nome do enum ("USUARIO", "IA") como texto
    @Column(name = "TP_REMETENTE", nullable = false)
    private Remetente remetente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CHAT", nullable = false)
    private Chat chat;

    // Construtor para facilitar a criação de novas mensagens
    public Historico(String conteudo, Remetente remetente, Chat chat) {
        this.conteudo = conteudo;
        this.remetente = remetente;
        this.chat = chat;
        this.dataEnvio = LocalDateTime.now();
    }
}