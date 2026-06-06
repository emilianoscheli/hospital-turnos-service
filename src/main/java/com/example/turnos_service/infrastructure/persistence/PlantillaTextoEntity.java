package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tsPlantillasTexto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaTextoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer idGrupoPlantilla;

    @Column(nullable = false)
    private Integer idMetodoEstudio;

    @Column(length = 50, nullable = false)
    private String codigo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;
}