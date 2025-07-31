package com.nutriai.api.service;

import com.nutriai.api.dto.usuario.UpdateUserDTO;
import com.nutriai.api.entity.Usuario;
import com.nutriai.api.exception.ResourceNotFoundException;
import com.nutriai.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /*** Busca todos os usuários cadastrados no banco de dados.
     * @return Uma lista de todos os usuários.     */

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /*** Busca um usuário pelo seu UID.
     * @param uid O UID do Firebase do usuário.
     * @return O objeto Usuario correspondente.
     * @throws ResourceNotFoundException se nenhum usuário for encontrado com o UID fornecido.*/

    public Usuario findByUid(String uid){
        return usuarioRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }


    /**Atualiza os dados de perfil de um usuário existente.
     * @param uid O ID do usuário a ser atualizado.
     * @param dto O DTO com as novas informações.
     * @return A entidade Usuario atualizada.*/

    @Transactional // Garante que a operação seja atômica
    public Usuario update(String uid, UpdateUserDTO dto) {
        Usuario usuario = this.findByUid(uid);

        usuario.setNome(dto.nomeCompleto());
        usuario.setCnpjCpf(dto.cnpjCpf());
        usuario.setCep(dto.cep());
        usuario.setCidade(dto.cidade());
        usuario.setRua(dto.rua());
        usuario.setNumero(dto.numero());

        return usuarioRepository.save(usuario);
    }


}
