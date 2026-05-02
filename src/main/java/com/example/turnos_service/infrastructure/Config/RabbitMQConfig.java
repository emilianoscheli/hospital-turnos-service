package com.example.turnos_service.infrastructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Agregamos la creación de la cola aquí también.
    // De esta forma, si Turnos arranca primero, crea la cola y no da error 404.
    @Bean
    public Queue queue() {
        return new Queue("paciente.sync.queue", true); // true = la cola sobrevive a reinicios de RabbitMQ
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}