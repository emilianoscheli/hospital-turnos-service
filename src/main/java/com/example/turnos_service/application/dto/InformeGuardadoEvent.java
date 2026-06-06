package com.example.turnos_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeGuardadoEvent {
    private Long idInforme;
    private Long idConsulta;
}