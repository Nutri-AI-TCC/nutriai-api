package com.nutriai.api.dto.paciente;

import com.nutriai.api.dto.usuario.UsuarioSummaryDTO;
import com.nutriai.api.entity.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PacienteResponseDTO(
        Long id,
        String nome,
        LocalDate nascimento,
        BigDecimal peso,
        BigDecimal altura,
        boolean ativo,
        UsuarioSummaryDTO usuario
) {
}
