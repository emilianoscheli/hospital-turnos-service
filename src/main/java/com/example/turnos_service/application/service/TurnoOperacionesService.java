package com.example.turnos_service.application.service;

import com.example.turnos_service.domain.exception.TurnoNoEncontradoException;
import com.example.turnos_service.domain.model.EstadoTurno;
import com.example.turnos_service.infrastructure.persistence.MedicoEntity;
import com.example.turnos_service.infrastructure.persistence.MedicoRepository;
import com.example.turnos_service.infrastructure.persistence.TurnoEntity;
import com.example.turnos_service.infrastructure.persistence.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TurnoOperacionesService {

    private final TurnoRepository turnoRepository;
    private final MedicoRepository medicoRepository;

    @Transactional
    public void confirmarAsistencia(Long idTurno) {
        TurnoEntity turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new TurnoNoEncontradoException("No existe el turno " + idTurno));

        // Validación basada en el MedicoEntity (Legacy mapeado)
        MedicoEntity medico = medicoRepository.findById(turno.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        if (!medico.getActivo()) {
            throw new RuntimeException("El médico no está atendiendo actualmente.");
        }

        // Usamos el Enum de dominio para mantener la lógica limpia
        turno.setIdEstadoConsulta(2); // 2 = Realizado/Confirmado en tu tabla tConsultas
        turnoRepository.save(turno);
    }
}