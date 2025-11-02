package com.nutriai.api.dto.dieta;

import jakarta.validation.constraints.NotBlank;

public record UpdateDietaDTO(
        @NotBlank(message = "O nome do documento não pode estar em branco")
        String nomeDocumento
) {
}
