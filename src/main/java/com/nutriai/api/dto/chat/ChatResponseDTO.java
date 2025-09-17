package com.nutriai.api.dto.chat;

import java.time.LocalDateTime;

/**
 * DTO para a resposta da API ao criar ou buscar um Chat.
 */

public record ChatResponseDTO(
        Long id,
        String titulo,
        LocalDateTime dataCriacao,
        Long pacienteId // Apenas o ID do paciente para evitar o loop
) {}