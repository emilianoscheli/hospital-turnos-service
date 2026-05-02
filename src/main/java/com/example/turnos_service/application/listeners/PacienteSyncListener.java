package com.example.turnos_service.application.listeners;

import com.example.turnos_service.application.dto.PacienteSyncDto;
import com.example.turnos_service.infrastructure.persistence.DatoPersonaEntity;
import com.example.turnos_service.infrastructure.persistence.DatoPersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PacienteSyncListener {

    private final DatoPersonaRepository datoPersonaRepository;

    @RabbitListener(queues = "paciente.sync.queue")
    public void recibirSincronizacionPaciente(PacienteSyncDto dto) {
        log.info("Mensaje recibido para sincronizar paciente ID: {}", dto.getIdEntidad());

        try {
            // Buscamos si ya existe por ID (el ID que viene de Admisión)
            DatoPersonaEntity entidadLocal = datoPersonaRepository.findById(dto.getIdEntidad())
                    .orElse(new DatoPersonaEntity());

            // Mapeamos los datos del DTO a la Entidad local de Turnos
            entidadLocal.setIdEntidad(dto.getIdEntidad());
            entidadLocal.setApellidoPaterno(dto.getApellidoPaterno());
            entidadLocal.setPrimerNombre(dto.getPrimerNombre());
            entidadLocal.setNumeroDocumento(dto.getNumeroDocumento());

            datoPersonaRepository.save(entidadLocal);
            log.info("Sincronización exitosa en Turnos para: {} {}", dto.getApellidoPaterno(), dto.getPrimerNombre());

        } catch (Exception e) {
            log.error("Error procesando mensaje de RabbitMQ en Turnos: {}", e.getMessage());
        }
    }
}