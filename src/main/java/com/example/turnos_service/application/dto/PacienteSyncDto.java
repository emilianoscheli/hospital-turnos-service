
package com.example.turnos_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteSyncDto {
    private Long idEntidad;
    private String apellidoPaterno;
    private String primerNombre;
    private String numeroDocumento;
}