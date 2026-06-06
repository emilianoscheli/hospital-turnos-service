package com.example.turnos_service.application.service;

import com.example.turnos_service.application.dto.EstudioPendienteDTO;
import com.example.turnos_service.application.dto.EstudioHistorialDTO;
import com.example.turnos_service.application.dto.EventoAgendaDTO;
import com.example.turnos_service.application.dto.TurnoCreateDTO;
import com.example.turnos_service.infrastructure.persistence.TurnoEntity;
import com.example.turnos_service.infrastructure.persistence.TurnoRepository;
import com.example.turnos_service.infrastructure.persistence.DatoPersonaRepository;
import com.example.turnos_service.infrastructure.persistence.EstudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final TurnoRepository turnoRepository;
    private final DatoPersonaRepository datoPersonaRepository;
    private final EstudioRepository estudioRepository;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Transactional
    public EventoAgendaDTO crearTurno(TurnoCreateDTO dto) {

        LocalDate fechaTurno = dto.getFecha();
        LocalTime horaTurno = dto.getHora();

        // 1. Validar si ya existe el turno
        boolean yaTieneTurno = turnoRepository.existsByIdPacienteAndIdMedicoAndFechaAndIdEstadoConsulta(
                dto.getIdPaciente(), dto.getIdProfesional(), fechaTurno, 1);

        if (yaTieneTurno) {
            throw new RuntimeException("El paciente ya tiene un turno asignado para ese día con este profesional.");
        }

        // 2. Armar la entidad principal
        TurnoEntity nuevoTurno = new TurnoEntity();
        nuevoTurno.setIdPaciente(dto.getIdPaciente());
        nuevoTurno.setIdMedico(dto.getIdProfesional());
        nuevoTurno.setIdServicioAtencion(dto.getIdServicio());
        nuevoTurno.setFecha(fechaTurno);
        nuevoTurno.setHora(horaTurno);
        nuevoTurno.setProgramada(1);

        // Mapeo de campos extra
        nuevoTurno.setIdUsuarioAsigno(dto.getIdUsuarioAsigno());
        nuevoTurno.setIdMotivoConsulta(dto.getMotivoConsulta());
        nuevoTurno.setIdTipoPrioridad(dto.getPrioridad());

        // --- 3. LÓGICA CONDICIONAL: Diagnóstico por Imágenes ---
        if (dto.getIdServicio() != null && dto.getIdServicio() == 3) {
            nuevoTurno.setIdSolicitante(dto.getSolicitante());
            nuevoTurno.setIdEstudio(dto.getEstudio());
            nuevoTurno.setDiagnostico(dto.getDiagnostico());

            nuevoTurno.setIdTipoConsulta(3);
            nuevoTurno.setIdEstadoConsulta(1);
        } else {
            nuevoTurno.setIdTipoConsulta(1);
            nuevoTurno.setIdEstadoConsulta(1);
        }

        // 4. Persistir en PostgreSQL
        TurnoEntity turnoGuardado = turnoRepository.save(nuevoTurno);

        // 5. Armar la respuesta
        String nombrePaciente = datoPersonaRepository.findById(dto.getIdPaciente())
                .map(p -> p.getApellidoPaterno() + " " + p.getPrimerNombre())
                .orElse("PACIENTE DESCONOCIDO");

        LocalDateTime startDt = LocalDateTime.of(turnoGuardado.getFecha(), turnoGuardado.getHora());
        LocalDateTime endDt = startDt.plusMinutes(15);

        // Construimos el mapa de propiedades de forma dinámica
        Map<String, Object> props = new HashMap<>();
        props.put("tipo", "turno");
        props.put("idPaciente", turnoGuardado.getIdPaciente());
        props.put("estadoConsulta", turnoGuardado.getIdEstadoConsulta());

        return EventoAgendaDTO.builder()
                .id(turnoGuardado.getId().toString())
                .title(nombrePaciente)
                .start(startDt.format(ISO_FORMATTER))
                .end(endDt.format(ISO_FORMATTER))
                .extendedProps(props)
                .build();
    }

    @Transactional
    public void anularTurno(Long idTurno, Integer idMotivo) {
        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("El turno no existe"));

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

            // 1. Armamos las propiedades base
            Map<String, Object> props = new HashMap<>();
            props.put("tipo", "turno");
            props.put("idPaciente", turno.getIdPaciente());
            props.put("estadoConsulta", turno.getIdEstadoConsulta());
            props.put("idServicio", turno.getIdServicioAtencion() != null ? turno.getIdServicioAtencion() : "");

            // 2. Inyectamos los datos de DXI si corresponde
            if (turno.getIdServicioAtencion() != null && turno.getIdServicioAtencion() == 3) {
                props.put("diagnostico", turno.getDiagnostico());

                if (turno.getIdEstudio() != null) {
                    estudioRepository.findById(turno.getIdEstudio())
                            .ifPresent(est -> props.put("estudio", est.getNombre()));
                }
            }

            return EventoAgendaDTO.builder()
                    .id(turno.getId().toString())
                    .title(nombrePaciente)
                    .start(startDt.format(ISO_FORMATTER))
                    .end(endDt.format(ISO_FORMATTER))
                    .extendedProps(props)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<EventoAgendaDTO> obtenerAgendaPorServicio(Integer idServicio, String fecha) {
        LocalDate fechaLocal = LocalDate.parse(fecha);

        List<TurnoEntity> turnos = turnoRepository.findAgendaByServicioAndFecha(idServicio, fechaLocal);

        return turnos.stream().map(turno -> {
            LocalDateTime startDt = LocalDateTime.of(turno.getFecha(), turno.getHora());
            LocalDateTime endDt = startDt.plusMinutes(15);

            String nombrePaciente = turno.getDatosPaciente() != null ?
                    turno.getDatosPaciente().getApellidoPaterno() + " " + turno.getDatosPaciente().getPrimerNombre() :
                    "PACIENTE ID: " + turno.getIdPaciente();

            // 1. Armamos las propiedades base
            Map<String, Object> props = new HashMap<>();
            props.put("tipo", "turno");
            props.put("idPaciente", turno.getIdPaciente());
            props.put("estadoConsulta", turno.getIdEstadoConsulta());

            // 2. Inyectamos los datos de DXI si corresponde
            if (turno.getIdServicioAtencion() != null && turno.getIdServicioAtencion() == 3) {
                props.put("diagnostico", turno.getDiagnostico());

                if (turno.getIdEstudio() != null) {
                    estudioRepository.findById(turno.getIdEstudio())
                            .ifPresent(est -> props.put("estudio", est.getNombre()));
                }
            }

            return EventoAgendaDTO.builder()
                    .id(turno.getId().toString())
                    .title(nombrePaciente)
                    .start(startDt.format(ISO_FORMATTER))
                    .end(endDt.format(ISO_FORMATTER))
                    .extendedProps(props)
                    .build();
        }).collect(Collectors.toList());
    }

    // --- NUEVOS MÉTODOS PARA LOS INFORMES DE ESTUDIOS ---

    public List<EstudioPendienteDTO> obtenerEstudiosPendientes(Long idPaciente) {
        // Traemos las consultas que no tienen informe en el último año
        LocalDate fechaHaceUnAno = LocalDate.now().minusYears(1);
        return turnoRepository.findEstudiosPendientesPorPaciente(idPaciente, fechaHaceUnAno);
    }

    public List<EstudioHistorialDTO> obtenerHistorialEstudios(Long idPaciente) {
        // Traemos las consultas que SI tienen informe (no borrado) en el último año
        LocalDate fechaHaceUnAno = LocalDate.now().minusYears(1);
        return turnoRepository.findEstudiosHistorialPorPaciente(idPaciente, fechaHaceUnAno);
    }
}