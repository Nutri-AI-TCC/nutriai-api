package com.nutriai.api.service;

import com.nutriai.api.dto.chat.CreateChatDTO;
import com.nutriai.api.dto.chat.HistoricoResponseDTO;
import com.nutriai.api.entity.Chat;
import com.nutriai.api.entity.Historico;
import com.nutriai.api.entity.Paciente;
import com.nutriai.api.exception.ResourceNotFoundException;
import com.nutriai.api.repository.ChatRepository;
import com.nutriai.api.repository.HistoricoRepository;
import com.nutriai.api.repository.PacienteRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nutriai.api.dto.chat.ChatResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final PacienteService pacienteService;
    private final HistoricoRepository historicoRepository;

    // Construtor sem a dependência duplicada
    public ChatService(ChatRepository chatRepository, PacienteService pacienteService, HistoricoRepository historicoRepository) {
        this.chatRepository = chatRepository;
        this.pacienteService = pacienteService;
        this.historicoRepository = historicoRepository;
    }

    @Transactional
    public ChatResponseDTO create(CreateChatDTO dto, Long pacienteId, String nutricionistaUid) {
        Paciente paciente = pacienteService.findEntityByIdAndUsuarioUid(pacienteId, nutricionistaUid);

        Chat novoChat = new Chat();
        novoChat.setTitulo(dto.titulo());
        novoChat.setDataCriacao(LocalDateTime.now());
        novoChat.setPaciente(paciente);

        Chat chatSalvo = chatRepository.saveAndFlush(novoChat);

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

    /**
     * Busca o histórico de mensagens de um chat específico, validando a permissão do usuário.
     */
    @Transactional(readOnly = true)
    public List<HistoricoResponseDTO> getHistoricoDoChat(Long chatId, String nutricionistaUid) {
        // 1. Validação de Segurança e Posse
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado com o ID: " + chatId));

        if (!chat.getPaciente().getUsuario().getUid().equals(nutricionistaUid)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este chat.");
        }

        // 2. Busca o Histórico no banco de dados
        List<Historico> historico = historicoRepository.findByChatIdOrderByDataEnvioAsc(chatId);

        // 3. Converte a lista de Entidades para uma lista de DTOs
        return historico.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private HistoricoResponseDTO convertToDto(Historico historico) {
        return new HistoricoResponseDTO(
                historico.getId(),
                historico.getConteudo(),
                historico.getDataEnvio(),
                historico.getRemetente(),
                historico.getChat().getId()
        );
    }


    @Transactional(readOnly = true)
    public List<ChatResponseDTO> findAllByNutricionistaUid(String nutricionistaUid) {
        // 1. Usa o novo método do repositório para buscar as entidades
        List<Chat> chats = chatRepository.findAllByPaciente_Usuario_UidOrderByDataCriacaoDesc(nutricionistaUid);

        // 2. Converte a lista de entidades para uma lista de DTOs e a retorna
        return chats.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Deleta uma sessão de chat e todo o seu histórico, após validar a posse.
     * @param chatId O ID do chat a ser deletado.
     * @param nutricionistaUid O UID do nutricionista que está fazendo a requisição.
     * @throws ResourceNotFoundException se o chat não for encontrado.
     * @throws AccessDeniedException se o chat não pertencer ao nutricionista.
     */
    @Transactional
    public void delete(Long chatId, String nutricionistaUid) {
        // 1. Busca o chat pelo ID. Se não existir, lança uma exceção 404.
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado com o ID: " + chatId));

        // 2. VERIFICAÇÃO DE POSSE: Garante que o chat pertence ao usuário logado.
        if (!chat.getPaciente().getUsuario().getUid().equals(nutricionistaUid)) {
            throw new AccessDeniedException("Você não tem permissão para deletar este chat.");
        }

        // 3. Deleta o chat.
        // Graças ao @OneToMany(cascade = CascadeType.ALL) na entidade Chat,
        // ao deletar o chat, o JPA automaticamente deletará todas as mensagens (Historico) associadas.
        chatRepository.delete(chat);
    }


}