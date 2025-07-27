package com.nutriai.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserDTO(@NotBlank(message = "O e-mail não pode estar em branco")
                              @Email(message = "O formato de email fornecido é inválido")
                              String email,

                              @NotBlank(message = "O nome completo não pode estar em branco")
                              @Size(min = 6, message = "O nome deve ter no mínimo 6 caracteres")
                              String nomeCompleto,

                              @NotBlank(message = "A senha não pode estar em branco")
                              @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
                              String password,

                              @NotBlank(message = "A confirmação de senha não pode estar em branco")
                              String confirmarSenha,

                              @NotBlank(message = "O CPF/CNPJ não pode estar em branco")
                              String cpfCnpj,

                              @NotBlank(message = "O CEP não pode estar em branco")
                              String cep,

                              @NotBlank(message = "A cidade не pode estar em branco")
                              String cidade,

                              @NotBlank(message = "A rua não pode estar em branco")
                              String rua,

                              @NotBlank(message = "O número não pode estar em branco")
                              String numero
                              ) {
}
