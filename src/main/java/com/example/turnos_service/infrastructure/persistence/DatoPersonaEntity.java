package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "datopersona")
@Data
public class DatoPersonaEntity {

    @Id
    @Column(name = "identidad")
    private Long idEntidad;

    @Column(name = "apellidopaterno")
    private String apellidoPaterno;

    @Column(name = "primernombre")
    private String primerNombre;

    @Column(name = "numerodocumento")
    private String numeroDocumento;
}