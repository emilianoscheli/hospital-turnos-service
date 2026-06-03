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
    private final ObjectMapper objectMapper; // Para loguear el JSON exacto

    public NebulaIntegrationClient() {
        this.objectMapper = new ObjectMapper();
        String credentials = Base64.getEncoder().encodeToString("echeli:tero123".getBytes());

        // Actualizado a la IP pública
        this.restClient = RestClient.builder()
                .baseUrl("http://181.224.121.92:8181")
                .defaultHeader("Authorization", "Basic " + credentials)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void sendToRemote(CrypticPayloadDTO payload) {
        try {
            // Imprimimos el JSON exacto para comparar con el de PHP
            String jsonOutput = objectMapper.writeValueAsString(payload);
            System.out.println("[Client PACS] JSON que se va a enviar: " + jsonOutput);
            System.out.println("[Client PACS] Enviando POST a http://181.224.121.92:8181/restapi/worklist/ ...");

            ResponseEntity<Void> response = restClient.post()
                    .uri("/restapi/worklist/")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("[Client PACS] ¡Petición HTTP finalizada! Status: " + response.getStatusCode());
            } else {
                System.err.println("[Client PACS] El PACS rechazó la petición. Status Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("[Client PACS] EXCEPCIÓN Crítica al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}