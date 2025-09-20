package com.nutriai.api.config;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Configuração para criar o cliente de conexão com o Oracle Cloud Infrastructure (OCI).
 */
@Configuration
public class OciConfig {

    @Value("${oci.tenant.id}")
    private String tenantId;

    @Value("${oci.user.id}")
    private String userId;

    @Value("${oci.fingerprint}")
    private String fingerprint;

    @Value("${oci.region}")
    private String region;

    @Value("${oci.privatekey.path}")
    private Resource privateKeyResource;

    /**
     * Cria um bean do ObjectStorage, o cliente principal para interagir com o Object Storage.
     * @return Uma instância do cliente OCI Object Storage.
     */
    @Bean
    public ObjectStorage objectStorageClient() {

        Region ociRegion = Region.fromRegionCodeOrId(this.region);

        SimpleAuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
                .tenantId(tenantId)
                .userId(userId)
                .fingerprint(fingerprint)
                .region(ociRegion) // Usa o objeto Region convertido.
                .privateKeySupplier(() -> {
                    try {
                        return privateKeyResource.getInputStream();
                    } catch (IOException e) {
                        throw new RuntimeException("Não foi possível ler a chave privada da OCI", e);
                    }
                })
                .build();

        return ObjectStorageClient.builder().build(provider);
    }
}