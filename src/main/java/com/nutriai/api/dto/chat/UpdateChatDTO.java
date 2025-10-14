package com.nutriai.api.dto.chat;

import jakarta.validation.constraints.NotBlank;

public record UpdateChatDTO(
        @NotBlank(message = "O título não pode estar em branco")
        String titulo
) {
}
