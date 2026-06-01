package com.example.turnos_service.presentation.controller;

import com.example.turnos_service.application.dto.EventoAgendaDTO;
import com.example.turnos_service.application.dto.TurnoCreateDTO; // Import agregado
import com.example.turnos_service.application.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    public ResponseEntity<EventoAgendaDTO> crearTurno(@RequestBody TurnoCreateDTO turnoDTO) {
        EventoAgendaDTO nuevoEvento = agendaService.crearTurno(turnoDTO);
        return ResponseEntity.ok(nuevoEvento);
    }

    @GetMapping
    public ResponseEntity<List<EventoAgendaDTO>> getAgenda(
            @RequestParam Long idProfesional,
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta) {

        List<EventoAgendaDTO> agenda = agendaService.obtenerAgenda(idProfesional, fechaDesde, fechaHasta);
        return ResponseEntity.ok(agenda);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> anularTurno(
            @PathVariable Long id,
            @RequestParam Integer idMotivo) {

        agendaService.anularTurno(id, idMotivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/servicio")
    public ResponseEntity<List<EventoAgendaDTO>> getAgendaPorServicio(
            @RequestParam Integer idServicio,
            @RequestParam String fecha) {

        // Llamas a un nuevo método en tu AgendaService que use la query que acabamos de crear
        List<EventoAgendaDTO> agenda = agendaService.obtenerAgendaPorServicio(idServicio, fecha);

        return ResponseEntity.ok(agenda);
    }
}