package com.nutriai.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "USUARIOS", schema = "ADMIN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "UID_USUARIO")
    private String uid;

    @Column(name = "NOME_USUARIO", nullable = false)
    private String nome;

    @Column(name = "EMAIL_USUARIO", nullable = false, unique = true)
    private String email;

    @Column(name = "CNPJ_CPF_USUARIO")
    private String cnpjCpf;

    @Column(name = "CEP_USUARIO")
    private String cep;

    @Column(name = "CIDADE_USUARIO")
    private String cidade;

    @Column(name = "RUA_USUARIO")
    private String rua;

    @Column(name = "NUMERO_USUARIO")
    private String numero;


    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paciente> pacientes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // ✅ Corrigido para Usuario
        Usuario usuario = (Usuario) o;
        // ✅ Corrigido para usar 'uid'
        return Objects.equals(uid, usuario.uid);
    }

    @Override
    public int hashCode() {
        // ✅ Corrigido para usar 'uid'
        return Objects.hash(uid);
    }


}
