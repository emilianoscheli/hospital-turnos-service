package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "tMedicos")
@Data
public class MedicoEntity {

    @Id
    @Column(name = "idEntidad")
    private Long idEntidad; // Comparte el mismo ID que DatoPersona

    // Relación con DatoPersona (INNER JOIN automático)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEntidad", referencedColumnName = "idEntidad", insertable = false, updatable = false)
    private DatoPersonaEntity datosPersonales;

    // Relación con tsEspecialidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEspecialidad")
    private EspecialidadEntity especialidad;

    // Relación ManyToMany para tMedicosPorServicio
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tMedicosPorServicio",
            joinColumns = @JoinColumn(name = "idEntidadMedico"),
            inverseJoinColumns = @JoinColumn(name = "idServicio")
    )
    private List<ServicioEntity> servicios;

    // Tu regla estricta: Borrado lógico (En el legacy era activo = 1)
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}