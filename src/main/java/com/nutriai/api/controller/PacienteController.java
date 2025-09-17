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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
     * ✅ MÉTODO CREATE CORRIGIDO: Unificado e retornando o cabeçalho Location.
     */
    @PostMapping
    public ResponseEntity<PacienteResponseDTO> createPaciente(@Valid @RequestBody CreatePacienteDTO dto,
                                                              Authentication authentication) {
        String nutricionistaUid = authentication.getName();
        PacienteResponseDTO responseDto = pacienteService.create(dto, nutricionistaUid);

        // Constrói a URI para o novo recurso (ex: /api/v1/pacientes/82)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();

        // Retorna 201 Created com a URL e o corpo do objeto criado
        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> getMeusPacientes(Authentication authentication) {
        String nutricionistaUid = authentication.getName();
        List<PacienteResponseDTO> pacientes = pacienteService.findByUsuarioUid(nutricionistaUid);
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> getPacienteById(@PathVariable Long id, Authentication authentication) {
        String nutricionistaUid = authentication.getName();
        PacienteResponseDTO paciente = pacienteService.findByIdAndUsuarioUid(id, nutricionistaUid);
        return ResponseEntity.ok(paciente);
    }

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