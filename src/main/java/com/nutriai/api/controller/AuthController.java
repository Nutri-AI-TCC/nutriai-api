package com.nutriai.api.controller;


import com.google.firebase.auth.FirebaseAuthException;
import com.nutriai.api.dto.auth.*;
import com.nutriai.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/** * Controller responsável por gerenciar os endpoints de autenticação de usuários.
 * Inclui o registro e o login. */

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World!");
    }

    /*** Endpoint para registrar um novo usuário no sistema.
     * Recebe e-mail e senha, e cria uma nova conta no Firebase.*/

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) throws FirebaseAuthException {
        authService.create(registerUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("E-mail registrado com sucesso!");
    }


    /**     * Endpoint para autenticar um usuário existente e retornar os tokens de acesso.*/
    @PostMapping("/login")
    public ResponseEntity<FirebaseSignInResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        FirebaseSignInResponse response = authService.login(loginRequest.email(), loginRequest.password());
        return ResponseEntity.ok(response);
    }

    /** Endpoint para renovar um idToken usando um refreshToken.     */
    
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenApiRequest request) {
        RefreshTokenResponse response = authService.exchangeRefreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

}
