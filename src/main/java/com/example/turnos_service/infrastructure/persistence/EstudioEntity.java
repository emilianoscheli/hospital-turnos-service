package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "testudios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "estudio", nullable = false, length = 64)
    private String nombre;

    @Column(name = "idMetodoEstudio", nullable = false)
    private Integer idMetodoEstudio;

    @Column(name = "idDiagnosticoPredeterminado", nullable = false)
    private Long idDiagnosticoPredeterminado;
}