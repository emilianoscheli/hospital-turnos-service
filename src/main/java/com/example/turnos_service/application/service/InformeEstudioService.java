package com.example.turnos_service.application.service;

import com.example.turnos_service.application.dto.InformeRequestDTO;
import com.example.turnos_service.application.dto.InformeGuardadoEvent;
import com.example.turnos_service.infrastructure.persistence.InformeEstudioEntity;
import com.example.turnos_service.infrastructure.persistence.InformeEstudioRepository;
import com.example.turnos_service.infrastructure.persistence.TurnoRepository;
import com.example.turnos_service.infrastructure.persistence.MedicoRepository;
import com.example.turnos_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor // Lombok te crea el constructor con todos los "final"
public class InformeEstudioService {

    private final InformeEstudioRepository informeRepository;
    private final TurnoRepository turnoRepository;
    private final MedicoRepository medicoRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Long guardarInforme(InformeRequestDTO request) {

        // 1. Manejo del Soft Delete si ya existía un informe
        informeRepository.findByTurnoIdAndFechaHoraBorradoSuaveIsNull(request.getIdConsulta())
                .ifPresent(informeViejo -> {
                    informeViejo.setFechaHoraBorradoSuave(LocalDateTime.now());
                    informeRepository.save(informeViejo);
                });

        // 2. Mapeo manual desde el DTO a la Entidad
        InformeEstudioEntity nuevoInforme = new InformeEstudioEntity();

        // Usamos referencias proxy para evitar SELECTs extras
        nuevoInforme.setTurno(turnoRepository.getReferenceById(request.getIdConsulta()));
        nuevoInforme.setMedico(medicoRepository.getReferenceById(request.getIdMedico()));

        nuevoInforme.setFecha(request.getFecha());
        nuevoInforme.setTitulo(request.getTitulo());
        nuevoInforme.setInformeTextual(request.getInformeTextual());
        nuevoInforme.setFechaControl(request.getFechaControl());

        // Convertimos el array de plantillas a un string separado por comas
        if (request.getPlantillasSeleccionadas() != null && !request.getPlantillasSeleccionadas().isEmpty()) {
            nuevoInforme.setPlantillasSeleccionadas(String.join(",", request.getPlantillasSeleccionadas()));
        }

        nuevoInforme.setCategoriaBIRADS(request.getCategoriaBIRADS());
        nuevoInforme.setDensidadBIRADS(request.getDensidadBIRADS());

        // 3. Guardar en Base de Datos
        InformeEstudioEntity informeGuardado = informeRepository.save(nuevoInforme);

        // 4. Publicar el evento en RabbitMQ de forma asíncrona (Fire and forget)
        InformeGuardadoEvent evento = new InformeGuardadoEvent(informeGuardado.getId(), request.getIdConsulta());
        rabbitTemplate.convertAndSend(RabbitMQConfig.INFORME_QUEUE_NAME, evento);

        return informeGuardado.getId();
    }
}