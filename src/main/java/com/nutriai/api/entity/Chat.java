package com.nutriai.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CHATS", schema = "ADMIN")
@Data
public class Chat {

    @Id
    @SequenceGenerator(name = "chat_seq_gen", sequenceName = "CHAT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_seq_gen")
    @Column(name = "ID_CHAT")
    private Long id;

    @Column(name = "NM_CHAT", nullable = false)
    private String titulo;

    @Column(name = "DT_CRIACAO_CHAT", nullable = false)
    private LocalDateTime dataCriacao;

    @Lob
    @Column(name = "DS_MENSAGENS_CHAT", nullable = false)
    private String mensagemChat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    private Paciente paciente;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Historico> historico;
}