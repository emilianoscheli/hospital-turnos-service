package com.example.turnos_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Turno {
    private Long id;
    private Long idPaciente;
    private Long idMedico;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private boolean activo;

    public boolean esFechaPasada() {
        return fecha.isBefore(LocalDate.now());
    }
}