package com.example.turnos_service.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetodoEstudioPorEspecialidadId implements Serializable {
    private Integer idMetodoEstudio;
    private Integer idEspecialidad;
}