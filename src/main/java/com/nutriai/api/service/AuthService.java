package com.nutriai.api.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.nutriai.api.client.FirebaseAuthClient;
import com.nutriai.api.dto.auth.FirebaseSignInResponse;
import com.nutriai.api.dto.auth.RefreshTokenResponse;
import com.nutriai.api.exception.AccountAlreadyExistsException;
import org.springframework.stereotype.Service;


/** * Classe de serviço que orquestra as operações relacionadas à autenticação de usuários.
 sendo responsável por interagir com os componentes do Firebase para criar e logar usuários. */


@Service
public class AuthService {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseAuthClient firebaseAuthClient;

    public AuthService(FirebaseAuth firebaseAuth, FirebaseAuthClient firebaseAuthClient) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseAuthClient = firebaseAuthClient;
    }


    private static final String DUPLICATE_ACCOUNT_ERROR = "EMAIL_EXISTS";

    public void create(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setEmailVerified(Boolean.TRUE);

        try {
            firebaseAuth.createUser(request);
        } catch (FirebaseAuthException exception) {
            if (exception.getMessage().contains(DUPLICATE_ACCOUNT_ERROR)) {
                throw new AccountAlreadyExistsException("A conta com o e-mail fornecido já existe.");
            }
            throw exception;
        }

    }


    public FirebaseSignInResponse login(String email, String password) {
        return firebaseAuthClient.login(email, password);
    }


    public RefreshTokenResponse exchangeRefreshToken(String refreshToken) {
        return firebaseAuthClient.exchangeRefreshToken(refreshToken);
    }



}
