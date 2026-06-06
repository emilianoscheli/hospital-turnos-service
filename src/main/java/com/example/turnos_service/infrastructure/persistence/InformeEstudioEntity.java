package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tInformesEstudios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeEstudioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ACÁ ESTÁ LA MAGIA: Lo relacionamos directo a tu TurnoEntity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idConsulta", nullable = false)
    private TurnoEntity turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMedico", nullable = false)
    private MedicoEntity medico; // Ojo, esta es la entidad del médico que REDACTA el informe, no el solicitante

    private LocalDate fecha;

    @Column(length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String informeTextual;

    private LocalDate fechaControl;

    // --- Manejo de Soft Delete ---
    private LocalDateTime fechaHoraBorradoSuave;

    // --- Campos específicos de Métodos (Ej: Mamo = 4) ---
    private Integer numero;
    private Integer categoriaBIRADS;
    private Integer densidadBIRADS;

    @Column(columnDefinition = "VARCHAR(255)")
    private String plantillasSeleccionadas;
}