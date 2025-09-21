package com.nutriai.api.repository;

import com.nutriai.api.entity.Dieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Long> {

    List<Dieta> findAllByPacienteId(Long pacienteId);

}
