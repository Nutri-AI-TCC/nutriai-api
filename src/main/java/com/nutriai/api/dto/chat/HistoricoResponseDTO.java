package com.nutriai.api.dto.chat;

import com.nutriai.api.entity.Remetente;
import java.time.LocalDateTime;

public record HistoricoResponseDTO(
        Long id,
        String conteudo,
        LocalDateTime dataEnvio,
        Remetente remetente,
        Long chatId
) {}