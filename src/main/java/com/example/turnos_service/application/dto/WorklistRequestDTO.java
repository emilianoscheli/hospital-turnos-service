
package com.example.turnos_service.application.dto;

import java.io.Serializable;

// Captura lo que manda tu frontend directamente
public record WorklistRequestDTO(
        Long idTurno,
        Long idPaciente,
        String fechaRegistro
) implements Serializable {}