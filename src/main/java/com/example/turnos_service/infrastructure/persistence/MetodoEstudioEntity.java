package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tmetodosestudio") // Ajustá el nombre si en tu BD se llama distinto
@Data
public class MetodoEstudioEntity {

    @Id
    private Integer id;

    @Column(name = "descripcion") // Ajustá el nombre de la columna
    private String nombre;
}