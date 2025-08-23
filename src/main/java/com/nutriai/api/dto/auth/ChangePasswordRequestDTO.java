package com.nutriai.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDTO(
        @NotBlank(message = "A senha atual não pode estar em branco")
        String currentPassword,

        @NotBlank(message = "A nova senha não pode estar em branco")
        @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres")
        String newPassword,

        @NotBlank(message = "A confirmação de senha не pode estar em branco")
        String confirmPassword
) {
}
