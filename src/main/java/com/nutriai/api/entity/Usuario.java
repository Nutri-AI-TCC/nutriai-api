package com.nutriai.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "USUARIOS", schema = "ADMIN")
@Data
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


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paciente> pacientes;

}
