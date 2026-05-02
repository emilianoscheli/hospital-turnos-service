package com.example.turnos_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventoAgendaDTO {
    private String id;
    private String title;
    private String start;
    private String end;
    private String display;
    private Map<String, Object> extendedProps;
}