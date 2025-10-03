package com.nutriai.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CHATS", schema = "ADMIN")
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getMensagemChat() {
        return mensagemChat;
    }

    public void setMensagemChat(String mensagemChat) {
        this.mensagemChat = mensagemChat;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<Historico> getHistorico() {
        return historico;
    }

    public void setHistorico(List<Historico> historico) {
        this.historico = historico;
    }
}