package com.example.turnos_service.presentation.controller;

import com.example.turnos_service.application.dto.CatalogoDTO;
import com.example.turnos_service.infrastructure.persistence.EspecialidadRepository;
import com.example.turnos_service.infrastructure.persistence.MedicoRepository;
import com.example.turnos_service.infrastructure.persistence.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CatalogosController {

    private final ServicioRepository servicioRepository;
    private final EspecialidadRepository especialidadRepository;
    private final MedicoRepository medicoRepository;

    @GetMapping("/servicios")
    public ResponseEntity<List<CatalogoDTO>> getServicios() {
        List<CatalogoDTO> response = servicioRepository.findAll().stream()
                .map(s -> new CatalogoDTO(s.getId().toString(), s.getServicio()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 1. ELIMINÉ el método viejo getEspecialidades(@RequestParam) que causaba el choque.

    // 2. Traer TODAS las especialidades sin filtro
    @GetMapping("/especialidades")
    public ResponseEntity<List<CatalogoDTO>> getAllEspecialidades() {
        List<CatalogoDTO> response = especialidadRepository.findAll().stream()
                .map(e -> new CatalogoDTO(e.getId().toString(), e.getEspecialidad()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 3. Traer especialidades FILTRADAS por un servicio específico (Tu ruta RESTful)
    @GetMapping("/servicios/{idServicio}/especialidades")
    public ResponseEntity<List<CatalogoDTO>> getEspecialidadesPorServicio(@PathVariable Long idServicio) {
        List<CatalogoDTO> response = especialidadRepository.findEspecialidadesByServicioActivo(idServicio)
                .stream()
                .map(e -> new CatalogoDTO(e.getId().toString(), e.getEspecialidad()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profesionales")
    public ResponseEntity<List<CatalogoDTO>> getProfesionales(
            @RequestParam Long idEspecialidad,
            @RequestParam(required = false) Long idServicio) {

        var medicos = (idServicio != null)
                ? medicoRepository.findByServicioAndEspecialidadAndActivoTrue(idServicio, idEspecialidad)
                : medicoRepository.findByEspecialidadAndActivoTrueOrderByApellido(idEspecialidad);

        List<CatalogoDTO> response = medicos.stream()
                .map(m -> new CatalogoDTO(
                        m.getIdEntidad().toString(),
                        m.getDatosPersonales().getApellidoPaterno() + " " + m.getDatosPersonales().getPrimerNombre()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}