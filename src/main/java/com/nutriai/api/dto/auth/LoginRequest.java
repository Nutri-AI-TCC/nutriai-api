package com.nutriai.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "O e-mail não pode estar em branco")
                           @Email(message = "O formato do e-mail é inválido")
                           String email,

                           @NotBlank(message = "A senha não pode estar em branco")
                           String password) {
}
