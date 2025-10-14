package com.nutriai.api.controller;

import com.nutriai.api.dto.chat.ChatResponseDTO;
import com.nutriai.api.dto.chat.HistoricoResponseDTO;
import com.nutriai.api.dto.chat.UpdateChatDTO;
import com.nutriai.api.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    /**
     * ✅ NOVO ENDPOINT
     * Deleta uma sessão de chat específica pelo seu ID.
     * @param chatId O ID do chat a ser deletado, vindo da URL.
     * @param authentication Fornecido pelo Spring Security com os dados do usuário logado.
     * @return ResponseEntity com status 204 No Content em caso de sucesso.
     */
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @PathVariable Long chatId,
            Authentication authentication) {

        // Pega o UID do nutricionista logado.
        String nutricionistaUid = authentication.getName();

        // Chama o serviço para realizar a exclusão, que já contém a validação de segurança.
        chatService.delete(chatId, nutricionistaUid);

        // Retorna o status 204 No Content, o padrão para DELETE bem-sucedido.
        return ResponseEntity.noContent().build();
    }

    //Atualiza o título de uma sessão de chat específica.
    @PutMapping("/{chatId}")
    public ResponseEntity<ChatResponseDTO> updateChat(
            @PathVariable Long chatId,
            @RequestBody @Valid UpdateChatDTO dto,
            Authentication authentication) {

        // Pega o UID do nutricionista logado.
        String nutricionistaUid = authentication.getName();

        // Chama o serviço para realizar a atualização.
        ChatResponseDTO chatAtualizado = chatService.update(chatId, nutricionistaUid, dto);

        // Retorna o chat atualizado no corpo da resposta.
        return ResponseEntity.ok(chatAtualizado);
    }


}