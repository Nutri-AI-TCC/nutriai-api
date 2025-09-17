package com.nutriai.api.controller;

import com.nutriai.api.dto.chat.ChatResponseDTO;
import com.nutriai.api.dto.chat.CreateChatDTO;
import com.nutriai.api.dto.paciente.CreatePacienteDTO;
import com.nutriai.api.dto.paciente.PacienteResponseDTO;
import com.nutriai.api.dto.paciente.UpdatePacienteDTO;
import com.nutriai.api.service.ChatService;
import com.nutriai.api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final ChatService chatService;

    public PacienteController(PacienteService pacienteService, ChatService chatService) {
        this.pacienteService = pacienteService;
        this.chatService = chatService;
    }

    /**
     * Endpoint para registrar um novo paciente para o nutricionista autenticado.
     * @param dto Os dados do paciente a ser criado.
     * @param authentication Fornecido pelo Spring Security, contém o UID do nutricionista logado.
     * @return ResponseEntity com o paciente criado e o status 201 Created.
     */

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> createPaciente(@Valid @RequestBody CreatePacienteDTO dto,
                                                   Authentication authentication) {

        String nutricionistaUid = authentication.getName();
        PacienteResponseDTO responseDto = pacienteService.create(dto, nutricionistaUid);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> getMeusPacientes(Authentication authentication) {
        String nutricionistaUid = authentication.getName();
        List<PacienteResponseDTO> pacientes = pacienteService.findByUsuarioUid(nutricionistaUid);
        return ResponseEntity.ok(pacientes);
    }

    /** Busca e retorna os dados de um paciente específico pelo seu ID.     */
    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> getPacienteById(@PathVariable Long id, Authentication authentication) {
        // Pega o UID do nutricionista logado
        String nutricionistaUid = authentication.getName();

        PacienteResponseDTO paciente = pacienteService.findByIdAndUsuarioUid(id, nutricionistaUid);

        return ResponseEntity.ok(paciente);
    }

    /** Atualiza os dados de um paciente existente. */

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> updatePaciente(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePacienteDTO dto,
            Authentication authentication) {

        String nutricionistaUid = authentication.getName();
        PacienteResponseDTO pacienteAtualizado = pacienteService.update(id, nutricionistaUid, dto);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @PostMapping("/{pacienteId}/chats")
    public ResponseEntity<ChatResponseDTO> createChat(
            @PathVariable Long pacienteId,
            @Valid @RequestBody CreateChatDTO dto,
            Authentication authentication) {

        String nutricionistaUid = authentication.getName();
        ChatResponseDTO novoChatDto = chatService.create(dto, pacienteId, nutricionistaUid);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoChatDto);
    }


}
