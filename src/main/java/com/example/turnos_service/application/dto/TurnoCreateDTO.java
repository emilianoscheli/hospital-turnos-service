package com.example.turnos_service.application.dto;

import lombok.Data;

@Data
public class TurnoCreateDTO {
    private Long idPaciente;
    private Long idProfesional;
    private Integer idServicio;

    // Estos campos los envía el frontend y faltaban en el DTO:
    private Integer idEspecialidad;
    private Long idUsuarioAsigno;
    private String motivoConsulta;
    private String prioridad;

    private String fecha;
    private String hora;
}