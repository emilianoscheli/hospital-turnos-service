package com.example.turnos_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoAgendaDTO {

    private String id;
    private String title;
    private String start;
    private String end;

    // Al usar un Map, Jackson (el conversor a JSON de Spring)
    // lo va a transformar perfectamente en el objeto { ... } que espera tu React
    private Map<String, Object> extendedProps;

}