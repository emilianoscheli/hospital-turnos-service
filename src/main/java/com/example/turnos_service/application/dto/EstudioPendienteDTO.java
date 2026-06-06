package com.example.turnos_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioPendienteDTO {
    private Long idConsulta;
    private LocalDate fecha;
    private String estudio;
    private String diagnostico;
    private String solicitante;
    private Integer idMetodoEstudio;
}