package com.nutriai.api.dto.auth;

public record FirebaseSignInRequest(String email, String password, boolean returnSecureToken) {
}
