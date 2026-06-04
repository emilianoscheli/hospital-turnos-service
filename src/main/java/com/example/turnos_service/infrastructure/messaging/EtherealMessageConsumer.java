package com.example.turnos_service.infrastructure.messaging;

import com.example.turnos_service.application.dto.CrypticPayloadDTO;
import com.example.turnos_service.application.service.MysticOrchestrator;
import com.example.turnos_service.infrastructure.client.NebulaIntegrationClient;
import com.example.turnos_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EtherealMessageConsumer {

    private final NebulaIntegrationClient integrationClient;
    // Agregamos la inyección del orquestador para poder usar su método de guardar en BD
    private final MysticOrchestrator mysticOrchestrator;

    @RabbitListener(queues = RabbitMQConfig.PHANTOM_QUEUE_NAME)
    public void consumeEtherealEvent(CrypticPayloadDTO payload) {
        System.out.println("[Consumer] Mensaje recibido desde RabbitMQ para Paciente ID: " + payload.getPatid());

        // 1. Ejecuta la petición HTTP y guarda el resultado (true o false)
        boolean pacsRespondioOk = integrationClient.sendToRemote(payload);

        // 2. Si el PACS respondió con éxito, actualizamos la base de datos
        if (pacsRespondioOk) {
            Long idTurno = Long.valueOf(payload.getId());
            // Llamamos al método que creaste en MysticOrchestrator (con el @Transactional)
            mysticOrchestrator.marcarComoAtendido(idTurno);
            System.out.println("[Consumer] Turno actualizado en base de datos a estado Realizado (2).");
        } else {
            System.err.println("[Consumer] Hubo un rechazo o error de conexión con el PACS. La BD NO se actualizó.");
        }

        System.out.println("[Consumer] Proceso de comunicación asíncrona finalizado.");
    }
}