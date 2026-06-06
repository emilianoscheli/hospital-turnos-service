package com.example.turnos_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeRequestDTO {
    private Long idConsulta;
    private Long idEstudio;
    private Integer idMetodoEstudio;
    private LocalDate fecha;
    private String titulo;
    private Long idMedico;
    private String informeTextual;
    private LocalDate fechaControl;
    private List<String> plantillasSeleccionadas;
    private Integer categoriaBIRADS;
    private Integer densidadBIRADS;
}