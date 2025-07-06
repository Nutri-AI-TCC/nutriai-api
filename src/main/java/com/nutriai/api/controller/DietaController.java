package com.nutriai.api.controller;


import com.nutriai.api.entity.Dieta;
import com.nutriai.api.service.DietaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dieta")
public class DietaController {

    private final DietaService dietaService;

    public DietaController(DietaService dietaService) {
        this.dietaService = dietaService;
    }

    @GetMapping
    public ResponseEntity<List<Dieta>> getAllDietas(){
        List<Dieta> allDietas = dietaService.getAllDietas();
        return ResponseEntity.ok(allDietas);
    }
}
