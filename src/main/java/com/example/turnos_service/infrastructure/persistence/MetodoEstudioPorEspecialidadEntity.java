package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tmetodosestudioporespecialidad")
@IdClass(MetodoEstudioPorEspecialidadId.class)
@Data
public class MetodoEstudioPorEspecialidadEntity {

    @Id
    @Column(name = "idmetodoestudio")
    private Integer idMetodoEstudio;

    @Id
    @Column(name = "idespecialidad")
    private Integer idEspecialidad;
}