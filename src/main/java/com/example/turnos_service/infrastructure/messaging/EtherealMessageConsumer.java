package com.example.turnos_service.infrastructure.messaging;

import com.example.turnos_service.application.dto.CrypticPayloadDTO;
import com.example.turnos_service.infrastructure.client.NebulaIntegrationClient;
import com.example.turnos_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EtherealMessageConsumer {

    private final NebulaIntegrationClient integrationClient;

    @RabbitListener(queues = RabbitMQConfig.PHANTOM_QUEUE_NAME)
    public void consumeEtherealEvent(CrypticPayloadDTO payload) {
        System.out.println("[Consumer] Mensaje recibido desde RabbitMQ para Paciente ID: " + payload.getPatid());

        // Ejecuta la petición HTTP pesada hacia el PACS fuera del flujo del usuario
        integrationClient.sendToRemote(payload);

        System.out.println("[Consumer] Proceso de comunicación con el PACS finalizado.");
    }
}