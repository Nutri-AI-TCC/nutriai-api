package com.nutriai.api.client;

import com.nutriai.api.dto.auth.FirebaseSignInRequest;
import com.nutriai.api.dto.auth.FirebaseSignInResponse;
import com.nutriai.api.exception.InvalidLoginCredentialsException;
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

    private static final String API_KEY_PARAM = "key";
    private static final String INVALID_CREDENTIALS_ERROR = "INVALID_LOGIN_CREDENTIALS";
    private static final String SIGN_IN_BASE_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword";

    @Value("${com.nutriai.firebase.web-api-key}")
    private String webApiKey;


    /** Método público para autenticar um usuário. Prepara a requisição e delega o envio.*/

    public FirebaseSignInResponse login(String emailId, String password) {
        FirebaseSignInRequest requestBody = new FirebaseSignInRequest(emailId, password, true);
        return sendSignInRequest(requestBody);
    }


    /**Método privado que constrói e executa a requisição POST para a API do Firebase.*/

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

}
