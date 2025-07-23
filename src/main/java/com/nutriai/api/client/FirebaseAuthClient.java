package com.nutriai.api.client;

import com.nutriai.api.dto.auth.FirebaseSignInRequest;
import com.nutriai.api.dto.auth.FirebaseSignInResponse;
import com.nutriai.api.dto.auth.RefreshTokenRequest;
import com.nutriai.api.dto.auth.RefreshTokenResponse;
import com.nutriai.api.exception.InvalidLoginCredentialsException;
import com.nutriai.api.exception.InvalidRefreshTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


/*** Cliente HTTP para se comunicar com as APIs REST do Firebase Authentication.
 * Esta classe encapsula a lógica de fazer chamadas de rede para o Firebase,
 * mantendo o resto da aplicação desacoplado dos detalhes da API externa. */

@Component
public class FirebaseAuthClient {

    // --- Constantes e Variáveis de Configuração ---
    @Value("${com.nutriai.firebase.web-api-key}")
    private String webApiKey;
    private static final String API_KEY_PARAM = "key";

    // --- Constantes para Login ---
    private static final String INVALID_CREDENTIALS_ERROR = "INVALID_LOGIN_CREDENTIALS";
    private static final String SIGN_IN_BASE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword";

    // --- Constantes para Refresh Token (NOVO) ---
    private static final String REFRESH_TOKEN_BASE_URL = "https://securetoken.googleapis.com/v1/token";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
    private static final String INVALID_REFRESH_TOKEN_ERROR = "INVALID_REFRESH_TOKEN";




    /** Método público para autenticar um usuário. Prepara a requisição e delega o envio.*/

    public FirebaseSignInResponse login(String emailId, String password) {
        FirebaseSignInRequest requestBody = new FirebaseSignInRequest(emailId, password, true);
        return sendSignInRequest(requestBody);
    }


    /**Método privado que constrói e executa a requisição POST para a API de login do Firebase.*/

    private FirebaseSignInResponse sendSignInRequest(FirebaseSignInRequest firebaseSignInRequest) {
        try {
            return RestClient.create(SIGN_IN_BASE_URL)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(API_KEY_PARAM, webApiKey)
                            .build())
                    .body(firebaseSignInRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(FirebaseSignInResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getResponseBodyAsString().contains(INVALID_CREDENTIALS_ERROR)) {
                throw new InvalidLoginCredentialsException("Login ou senha são inválidos!");
            }
            throw exception;
        }
    }


    // --- Método de Refresh Token ---
    public RefreshTokenResponse exchangeRefreshToken(String refreshToken){
        RefreshTokenRequest requestBody = new RefreshTokenRequest(REFRESH_TOKEN_GRANT_TYPE, refreshToken);
        return sendRefreshTokenRequest(requestBody);
    }


    /**     * Envia a requisição para a API de troca de token do Firebase.     */

    private RefreshTokenResponse sendRefreshTokenRequest(RefreshTokenRequest refreshTokenRequest) {
        try {
            return RestClient.create(REFRESH_TOKEN_BASE_URL)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam(API_KEY_PARAM, webApiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(refreshTokenRequest)
                    .retrieve()
                    .body(RefreshTokenResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getResponseBodyAsString().contains(INVALID_REFRESH_TOKEN_ERROR)) {
                throw new InvalidRefreshTokenException("Refresh token inválido ou expirado.");
            }
            throw exception;
        }
    }


}
