package com.nutriai.api.service;

import com.nutriai.api.dto.paciente.CreatePacienteDTO;
import com.nutriai.api.dto.paciente.PacienteResponseDTO;
import com.nutriai.api.dto.paciente.UpdatePacienteDTO;
import com.nutriai.api.dto.usuario.UsuarioSummaryDTO;
import com.nutriai.api.entity.Paciente;
import com.nutriai.api.entity.Usuario;
import com.nutriai.api.exception.ResourceNotFoundException;
import com.nutriai.api.repository.PacienteRepository;
import org.springframework.security.access.AccessDeniedException;
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
    public PacienteResponseDTO create(CreatePacienteDTO dto, String usuarioUid) {
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

        Paciente pacienteSalvo = pacienteRepository.save(novoPaciente);

        // Converte para DTO antes de retornar
        return convertToDto(pacienteSalvo);
    }

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> findByUsuarioUid(String usuarioUid) {
        List<Paciente> pacientes = pacienteRepository.findByUsuarioUid(usuarioUid);
        return pacientes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO findByIdAndUsuarioUid(Long pacienteId, String usuarioUid) {
        Paciente paciente = findEntityByIdAndUsuarioUid(pacienteId, usuarioUid);
        return convertToDto(paciente);
    }

    /**
     * ✅ MÉTODO UPDATE CORRIGIDO: Chama o método de busca correto
     */
    @Transactional
    public PacienteResponseDTO update(Long pacienteId, String usuarioUid, UpdatePacienteDTO dto) {
        // Busca a entidade e já valida a posse
        Paciente paciente = findEntityByIdAndUsuarioUid(pacienteId, usuarioUid);

        paciente.setNome(dto.nome());
        paciente.setNascimento(dto.nascimento());
        paciente.setPeso(dto.peso());
        paciente.setAltura(dto.altura());
        paciente.setCnpjCpf(dto.cnpjCpf());
        paciente.setAlergias(dto.alergias());
        paciente.setComorbidades(dto.comorbidades());
        paciente.setMedicacoes(dto.medicacoes());
        paciente.setAtivo(dto.ativo());

        Paciente pacienteSalvo = pacienteRepository.save(paciente);
        return convertToDto(pacienteSalvo);
    }

    /**
     * Busca uma ENTIDADE Paciente, verificando a posse.
     * Este método é auxiliar para create, update e delete.
     */
    public Paciente findEntityByIdAndUsuarioUid(Long pacienteId, String usuarioUid) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o ID: " + pacienteId));

        if (!paciente.getUsuario().getUid().equals(usuarioUid)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este paciente.");
        }
        return paciente;
    }

    public PacienteResponseDTO convertToDto(Paciente paciente) {
        Usuario usuario = paciente.getUsuario();


        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getNascimento(),
                paciente.getPeso(),
                paciente.getAltura(),
                paciente.isAtivo(),
                paciente.getComorbidades(),
                paciente.getMedicacoes(),
                paciente.getCnpjCpf()
        );
    }
}