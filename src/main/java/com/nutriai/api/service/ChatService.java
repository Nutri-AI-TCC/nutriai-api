package com.nutriai.api.service;

import com.nutriai.api.dto.chat.CreateChatDTO;
import com.nutriai.api.entity.Chat;
import com.nutriai.api.entity.Paciente;
import com.nutriai.api.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nutriai.api.dto.chat.ChatResponseDTO;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final PacienteService pacienteService;

    public ChatService(ChatRepository chatRepository, PacienteService pacienteService) {
        this.chatRepository = chatRepository;
        this.pacienteService = pacienteService;
    }

    @Transactional
    public ChatResponseDTO create(CreateChatDTO dto, Long pacienteId, String nutricionistaUid) {
        // 1. Valida se o nutricionista é dono do paciente
        Paciente paciente = pacienteService.findByIdAndCheckOwnership(pacienteId, nutricionistaUid);

        // 2. Cria a nova entidade Chat
        Chat novoChat = new Chat();
        novoChat.setTitulo(dto.titulo());
        novoChat.setDataCriacao(LocalDateTime.now());
        novoChat.setPaciente(paciente);

        // 3. Salva no banco
        Chat chatSalvo = chatRepository.save(novoChat);

        // 4. Converte para DTO antes de retornar
        return convertToDto(chatSalvo);
    }

    /**
     * Método auxiliar para converter a entidade Chat em um DTO de resposta.
     */
    private ChatResponseDTO convertToDto(Chat chat) {
        return new ChatResponseDTO(
                chat.getId(),
                chat.getTitulo(),
                chat.getDataCriacao(),
                chat.getPaciente().getId() // Pega apenas o ID do paciente
        );
    }
}