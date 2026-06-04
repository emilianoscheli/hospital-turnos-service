package com.example.turnos_service.infrastructure.client;

import com.example.turnos_service.application.dto.CrypticPayloadDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Base64;

@Service
public class NebulaIntegrationClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public NebulaIntegrationClient() {
        this.objectMapper = new ObjectMapper();
        String credentials = Base64.getEncoder().encodeToString("echeli:tero123".getBytes());

        this.restClient = RestClient.builder()
                .baseUrl("http://181.224.121.92:8181")
                .defaultHeader("Authorization", "Basic " + credentials)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // CAMBIO: De void a boolean para saber si funcionó o falló
    public boolean sendToRemote(CrypticPayloadDTO payload) {
        try {
            String jsonOutput = objectMapper.writeValueAsString(payload);
            System.out.println("[Client PACS] JSON que se va a enviar: " + jsonOutput);
            System.out.println("[Client PACS] Enviando POST a http://181.224.121.92:8181/restapi/worklist/ ...");

            // CAMBIO: Cambiamos toBodilessEntity() por toEntity(String.class) para leer qué responde el PACS
            ResponseEntity<String> response = restClient.post()
                    .uri("/restapi/worklist/")
                    .body(payload)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("[Client PACS] ¡Petición HTTP finalizada! Status: " + response.getStatusCode());
                return true; // Le avisa al consumidor que todo salió OK
            } else {
                System.err.println("[Client PACS] El PACS rechazó la petición. Status Code: " + response.getStatusCode());
                // Esto te va a mostrar en consola qué campo exacto falló en el PACS
                System.err.println("[Client PACS] RESPUESTA DEL SERVIDOR (BODY): " + response.getBody());
                return false; // Le avisa al consumidor que falló
            }
        } catch (Exception e) {
            System.err.println("[Client PACS] EXCEPCIÓN Crítica al conectar: " + e.getMessage());
            e.printStackTrace();
            return false; // Le avisa al consumidor que falló por conexión
        }
    }
}