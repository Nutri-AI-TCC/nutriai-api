package com.nutriai.api.dto.paciente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePacienteDTO(@NotBlank(message = "O nome não pode estar em branco")
                                String nome,

                                @NotNull(message = "A data de nascimento não pode ser nula")
                                @PastOrPresent(message = "A data de nascimento não pode ser no futuro")
                                LocalDate nascimento,

                                @NotNull(message = "O peso não pode ser nulo")
                                BigDecimal peso,

                                @NotNull(message = "A altura não pode ser nula")
                                BigDecimal altura,

                                String cnpjCpf,
                                String alergias,
                                String comorbidades,
                                String medicacoes,
                                boolean ativo) {
}
