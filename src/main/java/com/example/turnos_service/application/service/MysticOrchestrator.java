package com.example.turnos_service.application.service;

import com.example.turnos_service.application.dto.CrypticPayloadDTO;
import com.example.turnos_service.infrastructure.persistence.TurnoEntity;
import com.example.turnos_service.infrastructure.persistence.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MysticOrchestrator {

    private final TurnoRepository turnoRepository;
    private final ArcaneEventPublisher publisher;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    @Transactional(readOnly = true)
    public void prepareAndDispatch(Long idTurno) {

        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado en la BD"));

        String pacienteNombreCompleto = turno.getDatosPaciente().getApellidoPaterno() + "^" +
                turno.getDatosPaciente().getPrimerNombre();

        // Extraemos el DNI real en lugar del ID autonumérico
        String dniReal = turno.getDatosPaciente().getNumeroDocumento() != null ?
                turno.getDatosPaciente().getNumeroDocumento().toString() :
                turno.getIdPaciente().toString();

        CrypticPayloadDTO payload = CrypticPayloadDTO.builder()
                .id(turno.getId().toString())
                .descr("ESTUDIO") // Reemplazar por sEstudio
                .date(turno.getFecha().format(DATE_FORMATTER))
                .starttime(turno.getHora().format(TIME_FORMATTER))
                .patid(dniReal) // ACÁ ESTABA EL ERROR PRINCIPAL
                .patname(pacienteNombreCompleto)
                .patsex("M") // TODO: Reemplazar por turno.getDatosPaciente().getSexo()
                .patbd("19900101") // TODO: Reemplazar por formato yyyyMMdd de fecha nac
                .origin("Ambulatorio")
                .modality("CT") // Reemplazar por modalidad real
                .aet("CT_SIEMENS")
                .referring(turno.getIdMedico().toString())
                .clinicaldata("Dolor en el pecho")
                .studyinstanceuid("")
                .status("SCHEDULED")
                .build();

        System.out.println("[Orchestrator] Payload construido para Turno ID: " + idTurno + " | DNI: " + dniReal);
        publisher.dispatch(payload);
    }
}