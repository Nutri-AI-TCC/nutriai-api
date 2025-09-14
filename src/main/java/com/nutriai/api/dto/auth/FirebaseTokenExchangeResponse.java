package com.nutriai.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FirebaseTokenExchangeResponse(
        @JsonProperty("id_token")
        String idToken,

        @JsonProperty("refresh_token")
        String refreshToken
) {
}
