package com.nutriai.api.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.nutriai.api.client.FirebaseAuthClient;
import com.nutriai.api.dto.auth.FirebaseSignInResponse;
import com.nutriai.api.dto.auth.RefreshTokenResponse;
import com.nutriai.api.dto.auth.RegisterUserDTO;
import com.nutriai.api.entity.Usuario;
import com.nutriai.api.exception.AccountAlreadyExistsException;
import com.nutriai.api.exception.PasswordMismatchException;
import com.nutriai.api.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


/** * Classe de serviço que orquestra as operações relacionadas à autenticação de usuários.
 sendo responsável por interagir com os componentes do Firebase para criar e logar usuários. */


@Service
public class AuthService {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseAuthClient firebaseAuthClient;
    private final UsuarioRepository usuarioRepository;

    public AuthService(FirebaseAuth firebaseAuth, FirebaseAuthClient firebaseAuthClient,
                       UsuarioRepository usuarioRepository) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseAuthClient = firebaseAuthClient;
        this.usuarioRepository = usuarioRepository;
    }


    private static final String DUPLICATE_ACCOUNT_ERROR = "EMAIL_EXISTS";

    @Transactional
    public void create(RegisterUserDTO dto) throws FirebaseAuthException {

        if (!dto.password().equals(dto.confirmarSenha())) {
            throw new PasswordMismatchException("As senhas não coincidem.");
        }

        // Cria o usuário no Firebase e captura o UID
        String firebaseUid = createFirebaseUser(dto.email(), dto.password());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setUid(firebaseUid);
        novoUsuario.setEmail(dto.email());
        novoUsuario.setNome(dto.nomeCompleto());
        novoUsuario.setCnpjCpf(dto.cpfCnpj());
        novoUsuario.setCep(dto.cep());
        novoUsuario.setCidade(dto.cidade());
        novoUsuario.setRua(dto.rua());
        novoUsuario.setNumero(dto.numero());
        usuarioRepository.save(novoUsuario);

    }

    private String createFirebaseUser(String email, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setEmailVerified(Boolean.TRUE);

        try {
            UserRecord userRecord = firebaseAuth.createUser(request);
            return userRecord.getUid();
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
