package com.nutriai.api.controller;

import com.nutriai.api.dto.chat.ChatResponseDTO;
import com.nutriai.api.dto.chat.HistoricoResponseDTO;
import com.nutriai.api.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats") // A rota base é para chats
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Retorna todo o histórico de mensagens de uma sessão de chat específica.
     */
    @GetMapping("/{chatId}/historico")
    public ResponseEntity<List<HistoricoResponseDTO>> getChatHistory(
            @PathVariable Long chatId,
            Authentication authentication) {

        String nutricionistaUid = authentication.getName();
        List<HistoricoResponseDTO> historico = chatService.getHistoricoDoChat(chatId, nutricionistaUid);

        return ResponseEntity.ok(historico);
    }

    @GetMapping
    public ResponseEntity<List<ChatResponseDTO>> getAllMyChats(Authentication authentication) {
        String nutricionistaUid = authentication.getName();
        List<ChatResponseDTO> chats = chatService.findAllByNutricionistaUid(nutricionistaUid);
        return ResponseEntity.ok(chats);
    }

}