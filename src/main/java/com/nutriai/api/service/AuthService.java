package com.nutriai.api.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.nutriai.api.client.FirebaseAuthClient;
import com.nutriai.api.dto.auth.ChangePasswordRequestDTO;
import com.nutriai.api.dto.auth.FirebaseSignInResponse;
import com.nutriai.api.dto.auth.RefreshTokenResponse;
import com.nutriai.api.dto.auth.RegisterUserDTO;
import com.nutriai.api.entity.Usuario;
import com.nutriai.api.exception.AccountAlreadyExistsException;
import com.nutriai.api.exception.CpfCnpjAlreadyExistsException;
import com.nutriai.api.exception.InvalidLoginCredentialsException;
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
    private final UsuarioService usuarioService;

    public AuthService(FirebaseAuth firebaseAuth, FirebaseAuthClient firebaseAuthClient, UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseAuthClient = firebaseAuthClient;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    private static final String DUPLICATE_ACCOUNT_ERROR = "EMAIL_EXISTS";

    @Transactional
    public void create(RegisterUserDTO dto) throws FirebaseAuthException {

        if (!dto.password().equals(dto.confirmarSenha())) {
            throw new PasswordMismatchException("As senhas não coincidem.");
        }

        if (usuarioRepository.existsByCnpjCpf(dto.cpfCnpj())) {
            throw new CpfCnpjAlreadyExistsException("O CPF/CNPJ informado já está em uso.");
        }

        // --- Lógica de Rollback ---
        String firebaseUid = null;
        try {
            // 1. Cria o usuário no Firebase e captura o UID
            firebaseUid = createFirebaseUser(dto.email(), dto.password());

            // 2. Tenta salvar no banco de dados
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

        } catch (Exception e) {
            // 3. Se um erro acontecer APÓS a criação do usuário no Firebase
            // DESFAZEMOS a criação do usuário no Firebase para evitar inconsistência.
            if (firebaseUid != null) {
                try {
                    firebaseAuth.deleteUser(firebaseUid);
                } catch (FirebaseAuthException firebaseEx) {
                    System.err.println("ERRO CRÍTICO: Falha ao fazer rollback do usuário no Firebase: " + firebaseUid);
                }
            }
            throw new RuntimeException("Erro durante o processo de registro. A operação foi revertida.", e);
        }
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

    /**
     * Altera a senha de um usuário autenticado no Firebase.
     * @param uid O UID do usuário logado.
     * @param dto O DTO contendo a nova senha e sua confirmação.
     * @throws FirebaseAuthException se ocorrer um erro ao se comunicar com o Firebase (ex: senha fraca).
     */
    public void changePassword(String uid, ChangePasswordRequestDTO dto) throws FirebaseAuthException {
        // 1. Valida se a nova senha e a confirmação são iguais.
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new PasswordMismatchException("A nova senha e a confirmação não coincidem.");
        }

        // 2. Busca o e-mail do usuário no banco de dados
        Usuario usuario = usuarioService.findByUid(uid);
        String email = usuario.getEmail();

        // 3. Verifica se a senha atual está correta.
        // Para isso, tentamos fazer um "login" com as credenciais fornecidas.
        try {
            firebaseAuthClient.login(email, dto.currentPassword());
        } catch (InvalidLoginCredentialsException e) {
            // Se o login falhar, a senha atual está incorreta.
            throw new InvalidLoginCredentialsException("A senha atual fornecida está incorreta.");
        }

        // 4. Se a verificação passou, cria a requisição para atualizar a senha.
        UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(uid)
                .setPassword(dto.newPassword());

        // 3. Comanda o Firebase para atualizar o usuário.
        this.firebaseAuth.updateUser(updateRequest);
    }


}
