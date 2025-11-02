package com.nutriai.api.controller;

import com.nutriai.api.dto.chat.ChatResponseDTO;
import com.nutriai.api.dto.chat.CreateChatDTO;
import com.nutriai.api.dto.dieta.DietaResponseDTO;
import com.nutriai.api.dto.dieta.UpdateDietaDTO;
import com.nutriai.api.dto.paciente.CreatePacienteDTO;
import com.nutriai.api.dto.paciente.PacienteResponseDTO;
import com.nutriai.api.dto.paciente.UpdatePacienteDTO;
import com.nutriai.api.service.ChatService;
import com.nutriai.api.service.DietaService;
import com.nutriai.api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final ChatService chatService;
    private final DietaService dietaService;

    public PacienteController(PacienteService pacienteService, ChatService chatService, DietaService dietaService) {
        this.pacienteService = pacienteService;
        this.chatService = chatService;
        this.dietaService = dietaService;
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
        ChatResponseDTO responseDto = chatService.create(dto, pacienteId, nutricionistaUid);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/chats/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @PostMapping("/{pacienteId}/dietas")
    public ResponseEntity<DietaResponseDTO> createDieta(
            @PathVariable Long pacienteId,
            @RequestParam("nomeDocumento") String nomeDocumento,
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication) throws IOException {

        String nutricionistaUid = authentication.getName();
        DietaResponseDTO novaDieta = dietaService.create(pacienteId, nutricionistaUid, nomeDocumento, arquivo);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaDieta);
    }

    @GetMapping("/{pacienteId}/dietas")
    public ResponseEntity<List<DietaResponseDTO>> getDietasByPaciente(
            @PathVariable Long pacienteId,
            Authentication authentication) {

        String nutricionistaUid = authentication.getName();
        List<DietaResponseDTO> dietas = dietaService.findAllByPaciente(pacienteId, nutricionistaUid);

        return ResponseEntity.ok(dietas);
    }

    @DeleteMapping("/{pacienteId}/dietas/{dietaId}")
    public ResponseEntity<Void> deleteDieta(
            @PathVariable Long pacienteId,
            @PathVariable Long dietaId,
            Authentication authentication) {

        String nutricionistaUid = authentication.getName();

        dietaService.delete(pacienteId, dietaId, nutricionistaUid);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{pacienteId}/dietas/{dietaId}")
    public ResponseEntity<DietaResponseDTO> updateNomeDieta(
            @PathVariable Long pacienteId,
            @PathVariable Long dietaId,
            @Valid @RequestBody UpdateDietaDTO dto,
            Authentication authentication) {

        String nutricionistaUid = authentication.getName();
        DietaResponseDTO dietaAtualizada = dietaService.updateNomeDocumento(pacienteId, dietaId, nutricionistaUid, dto);

        return ResponseEntity.ok(dietaAtualizada);
    }

}