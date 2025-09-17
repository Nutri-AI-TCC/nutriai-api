package com.nutriai.api.dto.chat;

import jakarta.validation.constraints.NotBlank;

public record SendMessageDTO(
        @NotBlank(message = "O conteúdo da mensagem não pode estar em branco")
        String conteudo
) {}