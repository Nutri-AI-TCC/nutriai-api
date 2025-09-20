package com.nutriai.api.controller;

import com.nutriai.api.dto.dieta.DietaResponseDTO;
import com.nutriai.api.service.DietaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pacientes/{pacienteId}/dietas")
public class DietaController {

    private final DietaService dietaService;

    public DietaController(DietaService dietaService) {
        this.dietaService = dietaService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DietaResponseDTO> createDieta(
            @PathVariable Long pacienteId,
            @RequestParam("nomeDocumento") String nomeDocumento,
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication) throws IOException {

        String nutricionistaUid = authentication.getName();
        DietaResponseDTO novaDieta = dietaService.create(pacienteId, nutricionistaUid, nomeDocumento, arquivo);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaDieta);
    }
}