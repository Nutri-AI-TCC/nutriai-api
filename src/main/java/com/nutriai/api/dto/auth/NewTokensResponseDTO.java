package com.nutriai.api.dto.auth;

public record NewTokensResponseDTO(
        String idToken,
        String refreshToken
) {
}
