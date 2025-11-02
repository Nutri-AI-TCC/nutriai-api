package com.nutriai.api.service;

import com.nutriai.api.dto.dieta.DietaResponseDTO;
import com.nutriai.api.dto.dieta.UpdateDietaDTO;
import com.nutriai.api.entity.Dieta;
import com.nutriai.api.entity.Paciente;
import com.nutriai.api.exception.ResourceNotFoundException;
import com.nutriai.api.repository.DietaRepository;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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
                .map(dieta -> {
                    // Pega o nome do objeto (o que está salvo como arquivoUrl)
                    String objectName = extrairObjectNameDaUrl(dieta.getArquivoUrl());

                    // Gera uma nova PAR com validade curta
                    String temporaryAccessUrl = fileStorageService.createPreAuthenticatedRequest(
                            this.bucketName,
                            objectName,
                            CreatePreauthenticatedRequestDetails.AccessType.ObjectRead,
                            LocalDate.now().plusDays(1)
                    );

                    // Retorna o DTO com a URL segura e temporária
                    return new DietaResponseDTO(
                            dieta.getId(),
                            dieta.getNomeDocumento(),
                            temporaryAccessUrl, // <--- A URL gerada!
                            dieta.isAtivo(),
                            dieta.getPaciente().getId()
                    );
                })
                .collect(Collectors.toList());
    }

    private String extrairObjectNameDaUrl(String url) {
        if (url == null || !url.contains("/o/")) {
            return null;
        }
        return url.substring(url.indexOf("/o/") + 3);
    }


    @Transactional
    public void delete(Long pacienteId, Long dietaId, String nutricionistaUid) {
        // 1. Valida se o nutricionista é dono do paciente.
        // Isso também confirma que o paciente existe.
        pacienteService.findEntityByIdAndUsuarioUid(pacienteId, nutricionistaUid);

        // 2. Busca a dieta no banco de dados.
        Dieta dieta = dietaRepository.findById(dietaId)
                .orElseThrow(() -> new ResourceNotFoundException("Dieta não encontrada com o ID: " + dietaId));

        // 3. ✨ NOVA VALIDAÇÃO: Verifica se a dieta realmente pertence ao paciente da URL.
        if (!dieta.getPaciente().getId().equals(pacienteId)) {
            throw new AccessDeniedException("Esta dieta não pertence ao paciente informado.");
        }

        // O restante da lógica de exclusão do arquivo e do registro permanece o mesmo...

        // 4. Extrai o nome do objeto da URL para deletar do bucket.
        String arquivoUrl = dieta.getArquivoUrl();
        if (arquivoUrl != null && !arquivoUrl.isBlank()) {
            try {
                String objectName = arquivoUrl.substring(arquivoUrl.lastIndexOf("/o/") + 3);
                fileStorageService.delete(objectName);
            } catch (Exception e) {
                // É uma boa prática logar o erro em vez de apenas imprimir no console
                // logger.error("Falha ao deletar arquivo do Object Storage: {}", e.getMessage());
                System.err.println("Falha ao deletar arquivo do Object Storage: " + e.getMessage());
            }
        }

        // 5. Deleta o registro da dieta no banco de dados.
        dietaRepository.delete(dieta);
    }

    @Transactional
    public DietaResponseDTO updateNomeDocumento(Long pacienteId, Long dietaId, String nutricionistaUid, UpdateDietaDTO dto) {
        // 1. Valida se o nutricionista é dono do paciente
        pacienteService.findEntityByIdAndUsuarioUid(pacienteId, nutricionistaUid);

        // 2. Busca a dieta
        Dieta dieta = dietaRepository.findById(dietaId)
                .orElseThrow(() -> new ResourceNotFoundException("Dieta não encontrada com o ID: " + dietaId));

        // 3. Valida se a dieta pertence ao paciente
        if (!dieta.getPaciente().getId().equals(pacienteId)) {
            throw new AccessDeniedException("Esta dieta não pertence ao paciente informado.");
        }

        // 4. ATUALIZA O NOME
        dieta.setNomeDocumento(dto.nomeDocumento());
        Dieta dietaSalva = dietaRepository.save(dieta);

        // 5. Retorna o DTO
        return new DietaResponseDTO(
                dietaSalva.getId(),
                dietaSalva.getNomeDocumento(),
                dietaSalva.getArquivoUrl(),
                dietaSalva.isAtivo(),
                dietaSalva.getPaciente().getId()
        );
    }



}