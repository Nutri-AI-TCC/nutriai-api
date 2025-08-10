package com.nutriai.api.controller;

import com.nutriai.api.dto.paciente.CreatePacienteDTO;
import com.nutriai.api.dto.paciente.PacienteResponseDTO;
import com.nutriai.api.entity.Paciente;
import com.nutriai.api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /**
     * Endpoint para registrar um novo paciente para o nutricionista autenticado.
     * @param dto Os dados do paciente a ser criado.
     * @param authentication Fornecido pelo Spring Security, contém o UID do nutricionista logado.
     * @return ResponseEntity com o paciente criado e o status 201 Created.
     */

    @PostMapping
    public ResponseEntity<Paciente> createPaciente(@Valid @RequestBody CreatePacienteDTO dto,
                                                   Authentication authentication) {
        String nutricionistaUid = authentication.getName();
        Paciente pacienteSalvo = pacienteService.create(dto, nutricionistaUid);

        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteSalvo);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> getMeusPacientes(Authentication authentication) {
        String nutricionistaUid = authentication.getName();
        List<PacienteResponseDTO> pacientes = pacienteService.findByUsuarioUid(nutricionistaUid);
        return ResponseEntity.ok(pacientes);
    }


}
