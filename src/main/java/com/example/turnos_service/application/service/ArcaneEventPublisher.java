package com.example.turnos_service.application.service;

import com.example.turnos_service.application.dto.CrypticPayloadDTO;
import com.example.turnos_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArcaneEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void dispatch(CrypticPayloadDTO payload) {
        // Dispara el mensaje a la cola y se olvida (Fire and forget)
        rabbitTemplate.convertAndSend(RabbitMQConfig.PHANTOM_QUEUE_NAME, payload);
    }
}