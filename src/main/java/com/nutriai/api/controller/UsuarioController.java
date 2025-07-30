package com.nutriai.api.controller;

import com.nutriai.api.entity.Usuario;
import com.nutriai.api.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/me")
    public ResponseEntity<Usuario> getMyProfile(Authentication authentication) {
        String uid = authentication.getName();
        Usuario usuario = usuarioService.findByUid(uid);
        
        return ResponseEntity.ok(usuario);
    }


}
