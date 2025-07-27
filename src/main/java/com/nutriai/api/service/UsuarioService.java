package com.nutriai.api.service;

import com.nutriai.api.entity.Usuario;
import com.nutriai.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**     * Busca todos os usuários cadastrados no banco de dados.
     * @return Uma lista de todos os usuários.     */

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }


}
