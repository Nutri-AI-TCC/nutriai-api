package com.nutriai.api.service;

import com.nutriai.api.dto.paciente.CreatePacienteDTO;
import com.nutriai.api.entity.Paciente;
import com.nutriai.api.entity.Usuario;
import com.nutriai.api.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
