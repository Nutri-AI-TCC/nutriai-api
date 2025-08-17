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

        return convertToDto(pacienteSalvo);

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

    /** Busca um paciente específico pelo seu ID, garantindo que ele pertença ao nutricionista logado.
     * @param pacienteId O ID do paciente a ser buscado.
     * @param usuarioUid O UID do nutricionista que está fazendo a requisição.     */

    @Transactional(readOnly = true)
    public PacienteResponseDTO findByIdAndUsuarioUid(Long pacienteId, String usuarioUid) {
        // 1. Busca o paciente pelo ID.
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o ID: " + pacienteId));

        // 2. Garante que o paciente pertence ao usuário logado.
        if (!paciente.getUsuario().getUid().equals(usuarioUid)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este paciente.");
        }

        // 3. converte para DTO e retorna.
        return convertToDto(paciente);
    }

    /** Atualiza os dados de um paciente existente, após verificar a permissão do usuário.     */
    @Transactional
    public PacienteResponseDTO update(Long pacienteId, String usuarioUid, UpdatePacienteDTO dto) {
        // 1. Busca o paciente e verifica se ele pertence ao usuário logado.
        Paciente paciente = findByIdAndCheckOwnership(pacienteId, usuarioUid);

        // 2. Atualiza os campos da entidade com os dados do DTO.
        paciente.setNome(dto.nome());
        paciente.setNascimento(dto.nascimento());
        paciente.setPeso(dto.peso());
        paciente.setAltura(dto.altura());
        paciente.setCnpjCpf(dto.cnpjCpf());
        paciente.setAlergias(dto.alergias());
        paciente.setComorbidades(dto.comorbidades());
        paciente.setMedicacoes(dto.medicacoes());
        paciente.setAtivo(dto.ativo());

        // 3. Salva a entidade atualizada.
        Paciente pacienteSalvo = pacienteRepository.save(paciente);

        // 4. Converte a entidade atualizada para DTO e a retorna.
        return convertToDto(pacienteSalvo);
    }



    public PacienteResponseDTO convertToDto(Paciente paciente) {
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

    private Paciente findByIdAndCheckOwnership(Long pacienteId, String usuarioUid) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o ID: " + pacienteId));

        if (!paciente.getUsuario().getUid().equals(usuarioUid)) {
            throw new AccessDeniedException("Você não tem permissão para acessar este paciente.");
        }
        return paciente;
    }



}
