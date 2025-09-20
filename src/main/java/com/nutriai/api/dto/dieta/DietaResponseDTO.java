package com.nutriai.api.dto.dieta;

import java.time.LocalDateTime;

public record DietaResponseDTO(
        Long id,
        String nomeDocumento,
        String arquivoUrl,
        boolean ativo,
        Long pacienteId
) {}