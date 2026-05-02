package com.example.turnos_service.application.dto;

import lombok.Data;

@Data
public class TurnoCreateDTO {
    private Long idPaciente;
    private Long idProfesional;
    private Integer idServicio; // <-- Cambiar de Long a Integer
    private String fecha;
    private String hora;
}