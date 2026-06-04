package com.example.turnos_service.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "tconsultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "identidadpaciente", nullable = false)
    private Long idPaciente;

    @Column(name = "identidadmedico", nullable = false)
    private Long idMedico;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Column(name = "idtipoatencion")
    private Integer idTipoAtencion;

    @Column(name = "idestadoconsulta")
    private Integer idEstadoConsulta = 1;

    @Column(name = "idmotivoanulacionconsulta")
    private Integer idMotivoAnulacionConsulta;

    @Column(name = "idserviciodeatencion")
    private Integer idServicioAtencion;

    @Column(name = "idobrasocialporpersona")
    private Long idObraSocialPorPersona;

    @Column(name = "programada")
    private Integer programada = 1;

    @Column(name = "idlugaratencion")
    private Long idLugarAtencion;

    @Column(name = "fechahoraatencion")
    private LocalDateTime fechaHoraAtencion;

    @Column(name = "fechahorafinalizada")
    private LocalDateTime fechaHoraFinalizada;

    @Column(name = "idtipoprioridad")
    private Integer idTipoPrioridad;

    // 1. FUNDAMENTAL PARA AUDITORÍA (Quién dio el turno)
    @Column(name = "idusuarioasigno")
    private Long idUsuarioAsigno;

    // 2. FUNDAMENTAL PARA NEGOCIO (Primera vez, control, etc.)
    @Column(name = "idmotivoconsulta")
    private Integer idMotivoConsulta;

    // 3. (Opcional pero recomendado si tu hospital lo usa)
    @Column(name = "idtipoconsulta")
    private Integer idTipoConsulta = 1;

    // --- NUEVOS CAMPOS PARA DIAGNÓSTICO POR IMÁGENES ---

    @Column(name = "idsolicitante")
    private Long idSolicitante; // ID del médico que deriva

    @Column(name = "idestudio")
    private Long idEstudio; // ID de testudios

    @Column(name = "diagnostico")
    private String diagnostico; // Texto opcional

    // --- EL ARREGLO ESTÁ ACÁ ---
    // Hibernate une la tabla en memoria usando la columna identidadpaciente,
    // insertable = false, updatable = false le dice que es de SOLO LECTURA.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identidadpaciente", referencedColumnName = "identidad", insertable = false, updatable = false)
    private DatoPersonaEntity datosPaciente;


    @Transient
    public boolean isActivo() {
        return this.idEstadoConsulta != null && this.idEstadoConsulta != 3;
    }
}