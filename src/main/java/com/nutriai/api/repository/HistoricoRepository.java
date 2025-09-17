package com.nutriai.api.repository;

import com.nutriai.api.entity.Historico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico, Long> {

    /**
     * Busca todas as entradas de histórico para um chat específico,
     * ordenadas pela data de envio em ordem crescente.
     * @param chatId O ID do chat.
     * @return Uma lista de entradas do histórico.
     */
    List<Historico> findByChatIdOrderByDataEnvioAsc(Long chatId);
}