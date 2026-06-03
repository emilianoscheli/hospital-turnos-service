package com.example.turnos_service.presentation.controller;

import com.example.turnos_service.application.service.MysticOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agenda")
@RequiredArgsConstructor
public class ObscureTriggerController {

    private final MysticOrchestrator mysticOrchestrator;

    @PostMapping("/trigger-arcane")
    public ResponseEntity<Void> initProcess(@RequestBody Map<String, Long> payloadFront) {

        // Obtenemos solo el ID que manda el frontend
        Long idTurno = payloadFront.get("idTurno");

        if (idTurno == null) {
            return ResponseEntity.badRequest().build();
        }

        // Delegamos al servicio que busca en BD y envía a RabbitMQ
        mysticOrchestrator.prepareAndDispatch(idTurno);

        // Respondemos OK casi instantáneamente
        return ResponseEntity.accepted().build();
    }
}