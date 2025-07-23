package com.nutriai.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenApiRequest(
        @NotBlank String refreshToken) {
}
