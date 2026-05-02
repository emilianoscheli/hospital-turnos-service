package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tsServicios")
@Data
public class ServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "servicio")
    private String servicio;
}