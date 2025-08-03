package com.nutriai.api.controller;

import com.nutriai.api.dto.usuario.UpdateUserDTO;
import com.nutriai.api.entity.Usuario;
import com.nutriai.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> allUsers = usuarioService.findAll();
        return ResponseEntity.ok(allUsers);
    }


    //Atualiza os dados de perfil de um usuário existente com base no seu UID.

    @GetMapping("/me")
    public ResponseEntity<Usuario> getMyProfile(Authentication authentication) {
        String uid = authentication.getName();
        Usuario usuario = usuarioService.findByUid(uid);

        return ResponseEntity.ok(usuario);
    }

    //Endpoint para que o usuário autenticado atualize seus próprios dados de perfil.
    @PutMapping("/me")
    public ResponseEntity<Usuario> updateMyProfile(@Valid @RequestBody UpdateUserDTO dto,
            Authentication authentication) {

        String uid = authentication.getName();
        Usuario usuarioAtualizado = usuarioService.update(uid, dto);

        return ResponseEntity.ok(usuarioAtualizado);
    }


    /* Endpoint para que o usuário autenticado delete sua própria conta. */

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(Authentication authentication) {
        // Pega o UID do usuário logado.
        String uid = authentication.getName();
        usuarioService.delete(uid);

        return ResponseEntity.noContent().build();
    }


}
