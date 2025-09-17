package com.nutriai.api.dto.chat;

import jakarta.validation.constraints.NotBlank;

public record CreateChatDTO(@NotBlank String titulo) {
}
