package com.example.turnos_service.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

// Repositorios y Entidades
import com.example.turnos_service.infrastructure.persistence.TurnoRepository;
import com.example.turnos_service.infrastructure.persistence.MedicoRepository;
import com.example.turnos_service.infrastructure.persistence.PlantillaTextoRepository;
import com.example.turnos_service.infrastructure.persistence.PlantillaTextoEntity;

// --- IMPORTS CORREGIDOS A TU RUTA REAL ---
import com.example.turnos_service.application.dto.EstudioPendienteDTO;
import com.example.turnos_service.application.dto.EstudioHistorialDTO;
import com.example.turnos_service.application.dto.MedicoInformanteDTO;
import com.example.turnos_service.application.dto.InformeRequestDTO;
import com.example.turnos_service.application.service.InformeEstudioService;
// ------------------------------------------

@RestController
@RequestMapping("/api/v1")
public class InformeEstudioController {

    private final TurnoRepository turnoRepository;
    private final MedicoRepository medicoRepository;
    private final PlantillaTextoRepository plantillaRepository;
    private final InformeEstudioService informeService;

    public InformeEstudioController(TurnoRepository turnoRepository, MedicoRepository medicoRepository,
                                    PlantillaTextoRepository plantillaRepository, InformeEstudioService informeService) {
        this.turnoRepository = turnoRepository;
        this.medicoRepository = medicoRepository;
        this.plantillaRepository = plantillaRepository;
        this.informeService = informeService;
    }

    @GetMapping("/estudios/pendientes")
    public ResponseEntity<List<EstudioPendienteDTO>> getPendientes(@RequestParam Long idPaciente) {
        LocalDate haceUnAno = LocalDate.now().minusYears(1);
        return ResponseEntity.ok(turnoRepository.findEstudiosPendientesPorPaciente(idPaciente, haceUnAno));
    }

    @GetMapping("/estudios/historial")
    public ResponseEntity<List<EstudioHistorialDTO>> getHistorial(@RequestParam Long idPaciente) {
        LocalDate haceUnAno = LocalDate.now().minusYears(1);
        return ResponseEntity.ok(turnoRepository.findEstudiosHistorialPorPaciente(idPaciente, haceUnAno));
    }

    @GetMapping("/medicos/informantes")
    public ResponseEntity<List<MedicoInformanteDTO>> getMedicosInformantes() {
        return ResponseEntity.ok(medicoRepository.findMedicosInformantes());
    }

    @GetMapping("/plantillas")
    public ResponseEntity<List<PlantillaTextoEntity>> getPlantillas(@RequestParam Integer idMetodoEstudio, @RequestParam Integer idGrupo) {
        return ResponseEntity.ok(plantillaRepository.findByIdMetodoEstudioAndIdGrupoPlantilla(idMetodoEstudio, idGrupo));
    }

    @PostMapping("/informes")
    public ResponseEntity<Long> guardarInforme(@RequestBody InformeRequestDTO request) {
        Long idGenerado = informeService.guardarInforme(request);
        return ResponseEntity.status(201).body(idGenerado);
    }
}