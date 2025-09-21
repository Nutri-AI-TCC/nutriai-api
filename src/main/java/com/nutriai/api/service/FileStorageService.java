package com.nutriai.api.service;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Serviço responsável por interagir com o OCI Object Storage para upload de arquivos.
 */

@Service
public class FileStorageService {

    private final ObjectStorage objectStorageClient;

    @Value("${oci.objectstorage.namespace}")
    private String namespace;

    @Value("${oci.objectstorage.bucket-name}")
    private String bucketName;

    public FileStorageService(ObjectStorage objectStorageClient) {
        this.objectStorageClient = objectStorageClient;
    }

    /**
     * Faz o upload de um arquivo para o bucket da OCI.
     * @param file O arquivo enviado na requisição.
     * @param pathPrefix O prefixo do caminho (pasta) onde o arquivo será salvo no bucket.
     * @return O nome único do objeto salvo no bucket.
     * @throws IOException Se houver um erro de I/O.
     */
    public String upload(MultipartFile file, String pathPrefix) throws IOException {
        // Gera um nome de arquivo único para evitar colisões
        String fileName = pathPrefix + UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .objectName(fileName)
                .contentType(file.getContentType())
                .putObjectBody(file.getInputStream())
                .build();

        objectStorageClient.putObject(request);

        return fileName;
    }

    public void delete(String objectName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .objectName(objectName)
                .build();

        objectStorageClient.deleteObject(request);
    }

}