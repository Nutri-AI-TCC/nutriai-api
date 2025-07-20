package com.nutriai.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserDTO(@NotBlank(message = "O e-mail não pode estar em branco")
                              @Email(message = "O formato de email fornecido é inválido")
                              String email,

                              @NotBlank(message = "A senha não pode estar em branco")
                              String password) {
}
