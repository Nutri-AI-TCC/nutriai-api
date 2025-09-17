package com.nutriai.api.repository;

import com.nutriai.api.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByPaciente_Usuario_UidOrderByDataCriacaoDesc(String uid);

}
