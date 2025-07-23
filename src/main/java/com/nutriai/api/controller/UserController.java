package com.nutriai.api.controller;


import com.google.firebase.auth.FirebaseAuthException;
import com.nutriai.api.dto.auth.*;
import com.nutriai.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/** * Controller responsável por gerenciar os endpoints de autenticação de usuários.
 * Inclui o registro e o login. */

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /*** Endpoint para registrar um novo usuário no sistema.
     * Recebe e-mail e senha, e cria uma nova conta no Firebase.*/

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterUserDTO registerUserDTO) throws FirebaseAuthException {
        userService.create(registerUserDTO.email(), registerUserDTO.password());
        return ResponseEntity.status(HttpStatus.CREATED).body("E-mail registrado com sucesso!");
    }


    /**     * Endpoint para autenticar um usuário existente e retornar os tokens de acesso.*/
    @PostMapping("/login")
    public ResponseEntity<FirebaseSignInResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        FirebaseSignInResponse response = userService.login(loginRequest.email(), loginRequest.password());
        return ResponseEntity.ok(response);
    }

    /** Endpoint para renovar um idToken usando um refreshToken.     */
    
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenApiRequest request) {
        RefreshTokenResponse response = userService.exchangeRefreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

}
