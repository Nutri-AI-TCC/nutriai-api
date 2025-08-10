package com.nutriai.api.service;

import com.nutriai.api.dto.paciente.CreatePacienteDTO;
import com.nutriai.api.dto.paciente.PacienteResponseDTO;
import com.nutriai.api.dto.usuario.UsuarioSummaryDTO;
import com.nutriai.api.entity.Paciente;
import com.nutriai.api.entity.Usuario;
import com.nutriai.api.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioService usuarioService;

    public PacienteService(PacienteRepository pacienteRepository, UsuarioService usuarioService) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Paciente create(CreatePacienteDTO dto, String usuarioUid) {
        Usuario nutricionista = usuarioService.findByUid(usuarioUid);

        Paciente novoPaciente = new Paciente();
        novoPaciente.setNome(dto.nome());
        novoPaciente.setNascimento(dto.nascimento());
        novoPaciente.setPeso(dto.peso());
        novoPaciente.setAltura(dto.altura());
        novoPaciente.setCnpjCpf(dto.cnpjCpf());
        novoPaciente.setAlergias(dto.alergias());
        novoPaciente.setComorbidades(dto.comorbidades());
        novoPaciente.setMedicacoes(dto.medicacoes());
        novoPaciente.setAtivo(dto.ativo());
        novoPaciente.setUsuario(nutricionista);

        return pacienteRepository.save(novoPaciente);
    }


    /**Retorna uma lista de todos os pacientes pertencentes a um nutricionista específico.
     * @param usuarioUid O UID do nutricionista.
     * @return A lista de pacientes.     */

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> findByUsuarioUid(String usuarioUid) {
        List<Paciente> pacientes = pacienteRepository.findByUsuarioUid(usuarioUid);

        return pacientes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }


    private PacienteResponseDTO convertToDto(Paciente paciente) {
        Usuario usuario = paciente.getUsuario();

        UsuarioSummaryDTO usuarioDto = new UsuarioSummaryDTO(
                usuario.getUid(),
                usuario.getNome(),
                usuario.getEmail()
        );

        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getNascimento(),
                paciente.getPeso(),
                paciente.getAltura(),
                paciente.isAtivo(),
                usuarioDto
        );
    }



}
