package com.example.turnos_service.infrastructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // La cola que ya tenías
    @Bean
    public Queue pacienteSyncQueue() {
        return new Queue("paciente.sync.queue", true);
    }

    // NUEVA COLA para la integración asíncrona con el PACS
    public static final String PHANTOM_QUEUE_NAME = "phantom.dicom.queue";

    @Bean
    public Queue phantomDicomQueue() {
        return new Queue(PHANTOM_QUEUE_NAME, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    // --- NUEVA COLA PARA LOS INFORMES GUARDADOS ---
    public static final String INFORME_QUEUE_NAME = "informe.guardado.queue";

    @Bean
    public Queue informeGuardadoQueue() {
        return new Queue(INFORME_QUEUE_NAME, true);
    }
}