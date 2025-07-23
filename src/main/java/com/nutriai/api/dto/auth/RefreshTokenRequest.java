package com.nutriai.api.dto.auth;

public record RefreshTokenRequest(
        String grant_type,
        String refresh_token) {
}
