package com.nutriai.api.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        @NotBlank(message = "O nome completo não pode estar em branco")
        @Size(min = 6, message = "O nome deve ter no mínimo 6 caracteres")
        String nomeCompleto,

        @NotBlank(message = "O CPF/CNPJ не pode estar em branco")
        String cnpjCpf,

        @NotBlank(message = "O CEP não pode estar em branco")
        String cep,

        @NotBlank(message = "A cidade não pode estar em branco")
        String cidade,

        @NotBlank(message = "A rua não pode estar em branco")
        String rua,

        @NotBlank(message = "O número não pode estar em branco")
        String numero

        ) {
}
