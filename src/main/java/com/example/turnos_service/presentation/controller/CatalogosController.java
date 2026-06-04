package com.example.turnos_service.presentation.controller;

import com.example.turnos_service.infrastructure.persistence.MetodoEstudioEntity;
import com.example.turnos_service.application.dto.CatalogoDTO;
import com.example.turnos_service.infrastructure.persistence.EspecialidadRepository;
import com.example.turnos_service.infrastructure.persistence.MedicoRepository;
import com.example.turnos_service.infrastructure.persistence.ServicioRepository;
import com.example.turnos_service.infrastructure.persistence.EstudioRepository;
import com.example.turnos_service.infrastructure.persistence.MetodoEstudioRepository;
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

    // Inyectamos los nuevos repositorios
    private final MetodoEstudioRepository metodoEstudioRepository;
    private final EstudioRepository estudioRepository;

    @GetMapping("/servicios")
    public ResponseEntity<List<CatalogoDTO>> getServicios() {
        List<CatalogoDTO> response = servicioRepository.findAll().stream()
                .map(s -> new CatalogoDTO(s.getId().toString(), s.getServicio()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/especialidades")
    public ResponseEntity<List<CatalogoDTO>> getAllEspecialidades() {
        List<CatalogoDTO> response = especialidadRepository.findAll().stream()
                .map(e -> new CatalogoDTO(e.getId().toString(), e.getEspecialidad()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

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

    // --- NUEVOS ENDPOINTS PARA DXI ---

    @GetMapping("/solicitantes")
    public ResponseEntity<List<CatalogoDTO>> getSolicitantes() {
        List<CatalogoDTO> response = medicoRepository.findByActivoTrue().stream()
                .map(m -> new CatalogoDTO(
                        m.getIdEntidad().toString(),
                        m.getDatosPersonales().getApellidoPaterno() + " " + m.getDatosPersonales().getPrimerNombre()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estudios")
    public ResponseEntity<List<CatalogoDTO>> getEstudios(
            @RequestParam(required = false) Integer idMetodo) {

        var estudios = (idMetodo != null)
                ? estudioRepository.findByIdMetodoEstudio(idMetodo)
                : estudioRepository.findAll();

        List<CatalogoDTO> response = estudios.stream()
                // Asumo que tu EstudioEntity tiene getId() y getNombre()
                .map(e -> new CatalogoDTO(e.getId().toString(), e.getNombre()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Dejamos SOLO este método para los metodos de estudio
    @GetMapping("/estudios/metodos")
    public ResponseEntity<List<CatalogoDTO>> getMetodosEstudio(
            @RequestParam(required = false) Integer idEspecialidad) {

        List<MetodoEstudioEntity> metodos = (idEspecialidad != null)
                ? metodoEstudioRepository.findByEspecialidadNative(idEspecialidad)
                : metodoEstudioRepository.findAll();

        List<CatalogoDTO> response = metodos.stream()
                .map(m -> new CatalogoDTO(m.getId().toString(), m.getNombre()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}