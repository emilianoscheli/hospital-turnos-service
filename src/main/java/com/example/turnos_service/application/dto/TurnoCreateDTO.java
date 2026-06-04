package com.example.turnos_service.application.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TurnoCreateDTO {
    private Long idPaciente;
    private Integer idServicio;
    private Long idEspecialidad;
    private Long idProfesional;
    private Long idUsuarioAsigno;
    private LocalDate fecha;
    private LocalTime hora;

    // --- CORRECCIÓN: Pasar a Integer para recibir los IDs ---
    private Integer motivoConsulta;
    private Integer prioridad;

    // --- NUEVOS CAMPOS PARA DIAGNÓSTICO POR IMÁGENES (idServicio = 3) ---
    private Long solicitante;
    private Long estudio;
    private String diagnostico;
}