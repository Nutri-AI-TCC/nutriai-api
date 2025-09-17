package com.nutriai.api.controller;

import com.nutriai.api.dto.chat.ChatResponseDTO;
import com.nutriai.api.dto.chat.CreateChatDTO;
import com.nutriai.api.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pacientes/{pacienteId}/chats") // Rota aninhada
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDTO> createChat(
            @PathVariable Long pacienteId,
            @Valid @RequestBody CreateChatDTO dto,
            Authentication authentication) {

        String nutricionistaUid = authentication.getName();
        // O serviço agora retorna o DTO diretamente
        ChatResponseDTO novoChatDto = chatService.create(dto, pacienteId, nutricionistaUid);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoChatDto);
    }
}