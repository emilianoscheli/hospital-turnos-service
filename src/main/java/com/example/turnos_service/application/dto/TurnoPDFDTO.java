package com.example.turnos_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TurnoPDFDTO {
    // Datos equivalentes al script mPDF de tu legacy
    private String apellidoNombrePaciente;
    private String numeroDocumento;
    private Integer edad;
    private String obraSocial;
    private Integer numeroHistoriaClinica;
    private Integer numeroHistoriaClinicaAnterior;
    private String telefono;
    private String fechaHora;
}