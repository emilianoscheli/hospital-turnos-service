package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tsEspecialidades")
@Data
public class EspecialidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "especialidad")
    private String especialidad;
}