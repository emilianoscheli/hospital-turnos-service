package com.example.turnos_service.application.service;

import com.example.turnos_service.application.dto.EventoAgendaDTO;
import com.example.turnos_service.application.dto.TurnoCreateDTO;
import com.example.turnos_service.infrastructure.persistence.TurnoEntity;
import com.example.turnos_service.infrastructure.persistence.TurnoRepository;
import com.example.turnos_service.infrastructure.persistence.DatoPersonaRepository; // <-- Import agregado

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final TurnoRepository turnoRepository;
    private final DatoPersonaRepository datoPersonaRepository; // <-- ¡Descomentado para que funcione!

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Transactional
    public EventoAgendaDTO crearTurno(TurnoCreateDTO dto) {
        LocalDate fechaTurno = LocalDate.parse(dto.getFecha());
        LocalTime horaTurno = LocalTime.parse(dto.getHora());

        boolean yaTieneTurno = turnoRepository.existsByIdPacienteAndIdMedicoAndFechaAndIdEstadoConsulta(
                dto.getIdPaciente(), dto.getIdProfesional(), fechaTurno, 1);

        if (yaTieneTurno) {
            throw new RuntimeException("El paciente ya tiene un turno asignado para ese día con este profesional.");
        }

        TurnoEntity nuevoTurno = new TurnoEntity();
        nuevoTurno.setIdPaciente(dto.getIdPaciente());
        nuevoTurno.setIdMedico(dto.getIdProfesional());
        nuevoTurno.setIdServicioAtencion(dto.getIdServicio());
        nuevoTurno.setFecha(fechaTurno);
        nuevoTurno.setHora(horaTurno);
        nuevoTurno.setIdEstadoConsulta(1);
        nuevoTurno.setProgramada(1);

        TurnoEntity turnoGuardado = turnoRepository.save(nuevoTurno);

        // Buscamos el nombre localmente
        String nombrePaciente = datoPersonaRepository.findById(dto.getIdPaciente())
                .map(p -> p.getApellidoPaterno() + " " + p.getPrimerNombre())
                .orElse("PACIENTE DESCONOCIDO");

        LocalDateTime startDt = LocalDateTime.of(turnoGuardado.getFecha(), turnoGuardado.getHora());
        LocalDateTime endDt = startDt.plusMinutes(15);

        return EventoAgendaDTO.builder()
                .id(turnoGuardado.getId().toString())
                .title(nombrePaciente)
                .start(startDt.format(ISO_FORMATTER))
                .end(endDt.format(ISO_FORMATTER))
                .extendedProps(Map.of(
                        "tipo", "turno",
                        "idPaciente", turnoGuardado.getIdPaciente(),
                        "estadoConsulta", turnoGuardado.getIdEstadoConsulta()
                ))
                .build();
    }

    @Transactional
    public void anularTurno(Long idTurno, Integer idMotivo) {
        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("El turno no existe"));

        // Regla estricta: Borrado lógico cambiando el estado
        turno.setIdEstadoConsulta(3);
        turno.setIdMotivoAnulacionConsulta(idMotivo);

        turnoRepository.save(turno);
    }

    public List<EventoAgendaDTO> obtenerAgenda(Long idMedico, String fechaDesde, String fechaHasta) {
        LocalDate inicio = LocalDate.parse(fechaDesde);
        LocalDate fin = LocalDate.parse(fechaHasta);

        var turnos = turnoRepository.findAgendaByMedicoAndFechas(idMedico, inicio, fin);

        return turnos.stream().map(turno -> {
            LocalDateTime startDt = LocalDateTime.of(turno.getFecha(), turno.getHora());
            LocalDateTime endDt = startDt.plusMinutes(15);

            String nombrePaciente = turno.getDatosPaciente() != null ?
                    turno.getDatosPaciente().getApellidoPaterno() + " " + turno.getDatosPaciente().getPrimerNombre() :
                    "PACIENTE ID: " + turno.getIdPaciente();

            return EventoAgendaDTO.builder()
                    .id(turno.getId().toString())
                    .title(nombrePaciente)
                    .start(startDt.format(ISO_FORMATTER))
                    .end(endDt.format(ISO_FORMATTER))
                    .extendedProps(Map.of(
                            "tipo", "turno",
                            "idPaciente", turno.getIdPaciente(),
                            "estadoConsulta", turno.getIdEstadoConsulta(),
                            "idServicio", turno.getIdServicioAtencion() != null ? turno.getIdServicioAtencion() : ""
                    ))
                    .build();
        }).collect(Collectors.toList());
    }
    // Agrega esto dentro de tu clase AgendaService.java
    public List<EventoAgendaDTO> obtenerAgendaPorServicio(Integer idServicio, String fecha) {
        LocalDate fechaLocal = LocalDate.parse(fecha);

        // Llamamos a la nueva query que definiste en el repositorio
        List<TurnoEntity> turnos = turnoRepository.findAgendaByServicioAndFecha(idServicio, fechaLocal);

        return turnos.stream().map(turno -> {
            LocalDateTime startDt = LocalDateTime.of(turno.getFecha(), turno.getHora());
            LocalDateTime endDt = startDt.plusMinutes(15);

            String nombrePaciente = turno.getDatosPaciente() != null ?
                    turno.getDatosPaciente().getApellidoPaterno() + " " + turno.getDatosPaciente().getPrimerNombre() :
                    "PACIENTE ID: " + turno.getIdPaciente();

            return EventoAgendaDTO.builder()
                    .id(turno.getId().toString())
                    .title(nombrePaciente)
                    .start(startDt.format(ISO_FORMATTER))
                    .end(endDt.format(ISO_FORMATTER))
                    .extendedProps(Map.of(
                            "tipo", "turno",
                            "idPaciente", turno.getIdPaciente(),
                            "estadoConsulta", turno.getIdEstadoConsulta()
                    ))
                    .build();
        }).collect(Collectors.toList());
    }

}