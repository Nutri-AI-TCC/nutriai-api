package com.nutriai.api.service;

import com.nutriai.api.dto.dieta.DietaResponseDTO;
import com.nutriai.api.entity.Dieta;
import com.nutriai.api.entity.Paciente;
import com.nutriai.api.repository.DietaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DietaService {

    private final DietaRepository dietaRepository;
    private final PacienteService pacienteService;
    private final FileStorageService fileStorageService;

    @Value("${oci.objectstorage.namespace}")
    private String namespace;

    @Value("${oci.objectstorage.bucket-name}")
    private String bucketName;

    public DietaService(DietaRepository dietaRepository, PacienteService pacienteService, FileStorageService fileStorageService) {
        this.dietaRepository = dietaRepository;
        this.pacienteService = pacienteService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public DietaResponseDTO create(Long pacienteId, String nutricionistaUid, String nomeDocumento, MultipartFile arquivo) throws IOException {
        // 1. Valida se o nutricionista é dono do paciente.
        Paciente paciente = pacienteService.findEntityByIdAndUsuarioUid(pacienteId, nutricionistaUid);

        // 2. Faz o upload do arquivo para o bucket na OCI.
        String pathPrefix = "pacientes/" + pacienteId + "/dietas/";
        String objectName = fileStorageService.upload(arquivo, pathPrefix);

        // 3. Monta a URL completa do arquivo salvo.
        String arquivoUrl = "https://objectstorage.sa-saopaulo-1.oraclecloud.com/n/" + namespace + "/b/" + bucketName + "/o/" + objectName;

        // 4. Cria e salva a nova entidade Dieta no banco de dados.
        Dieta novaDieta = new Dieta();
        novaDieta.setNomeDocumento(nomeDocumento);
        novaDieta.setArquivoUrl(arquivoUrl);
        novaDieta.setAtivo(true);
        novaDieta.setPaciente(paciente);

        Dieta dietaSalva = dietaRepository.save(novaDieta);

        return convertToDto(dietaSalva);
    }

    private DietaResponseDTO convertToDto(Dieta dieta) {
        return new DietaResponseDTO(
                dieta.getId(),
                dieta.getNomeDocumento(),
                dieta.getArquivoUrl(),
                dieta.isAtivo(),
                dieta.getPaciente().getId()
        );
    }


    @Transactional(readOnly = true)
    public List<DietaResponseDTO> findAllByPaciente(Long pacienteId, String nutricionistaUid) {
        // 1. Valida se o nutricionista é dono do paciente. Se não for, uma exceção será lançada.
        pacienteService.findEntityByIdAndUsuarioUid(pacienteId, nutricionistaUid);

        // 2. Se a validação passar, busca as dietas associadas ao paciente.
        List<Dieta> dietas = dietaRepository.findAllByPacienteId(pacienteId);

        // 3. Converte a lista de entidades para uma lista de DTOs e a retorna.
        return dietas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


}