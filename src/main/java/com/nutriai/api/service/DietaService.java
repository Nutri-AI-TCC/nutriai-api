package com.nutriai.api.service;

import com.nutriai.api.entity.Dieta;
import com.nutriai.api.repository.DietaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class DietaService {

    private final DietaRepository dietaRepository;

    public DietaService(DietaRepository dietaRepository) {
        this.dietaRepository = dietaRepository;
    }

    public List<Dieta> getAllDietas() {
        return dietaRepository.findAll();
    }

}
