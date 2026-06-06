package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import com.example.turnos_service.application.dto.EstudioPendienteDTO;
import com.example.turnos_service.application.dto.EstudioHistorialDTO;

@Repository
public interface TurnoRepository extends JpaRepository<TurnoEntity, Long> {

    // Equivale a lo que hacías con la clase universal filtrando por médico y estado
    List<TurnoEntity> findByIdMedicoAndIdEstadoConsultaNot(Long idMedico, Integer estadoAnulado);

    // Valida si el paciente ya tiene un turno pendiente (estado 1) con ese médico ese día
    boolean existsByIdPacienteAndIdMedicoAndFechaAndIdEstadoConsulta(
            Long idPaciente, Long idMedico, LocalDate fecha, Integer idEstadoConsulta);

    // Para tu agenda de FullCalendar
    List<TurnoEntity> findByIdMedicoAndIdServicioAtencionAndIdEstadoConsultaNot(
            Long idMedico, Integer idServicio, Integer estadoAnulado);

    @Query("SELECT t FROM TurnoEntity t JOIN FETCH t.datosPaciente p " +
            "WHERE t.idMedico = :idMedico " +
            "AND t.fecha >= :fechaDesde AND t.fecha <= :fechaHasta " +
            "AND t.idEstadoConsulta != 3 " + // Regla estricta: No traer anulados
            "ORDER BY t.fecha ASC, t.hora ASC")
    List<TurnoEntity> findAgendaByMedicoAndFechas(
            @Param("idMedico") Long idMedico,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta);

    // --- ESTA QUERY ESTABA EN EL SERVICE Y LA MOVI ACA ---
    @Query("SELECT t FROM TurnoEntity t JOIN FETCH t.datosPaciente p " +
            "WHERE t.idServicioAtencion = :idServicio " +
            "AND t.fecha = :fecha " +
            "AND t.idEstadoConsulta != 3 " + // Excluir anulados
            "ORDER BY t.hora ASC")
    List<TurnoEntity> findAgendaByServicioAndFecha(
            @Param("idServicio") Integer idServicio,
            @Param("fecha") LocalDate fecha);

    // --- NUEVAS QUERIES PARA INFORMES DE ESTUDIOS ---

    /**
     * A. ENDPOINT PENDIENTES
     */
    @Query("SELECT new com.example.turnos_service.application.dto.EstudioPendienteDTO(" +
            "t.id, t.fecha, e.nombre, t.diagnostico, 'Dr. Solicitante', e.idMetodoEstudio) " +
            "FROM TurnoEntity t " +
            "LEFT JOIN EstudioEntity e ON t.idEstudio = e.id " +
            "LEFT JOIN InformeEstudioEntity i ON i.turno.id = t.id AND i.fechaHoraBorradoSuave IS NULL " +
            "WHERE t.idPaciente = :idPaciente " +
            "AND t.fecha >= :fechaLimite " +
            "AND t.idEstadoConsulta != 3 " +
            "AND i.id IS NULL")
    List<EstudioPendienteDTO> findEstudiosPendientesPorPaciente(
            @Param("idPaciente") Long idPaciente,
            @Param("fechaLimite") LocalDate fechaLimite);

    /**
     * B. ENDPOINT HISTORIAL
     */
    @Query("SELECT new com.example.turnos_service.application.dto.EstudioHistorialDTO(" +
            "t.id, t.fecha, e.nombre, t.diagnostico, 'Dr. Solicitante', i.informeTextual) " +
            "FROM TurnoEntity t " +
            "LEFT JOIN EstudioEntity e ON t.idEstudio = e.id " +
            "JOIN InformeEstudioEntity i ON i.turno.id = t.id " +
            "WHERE t.idPaciente = :idPaciente " +
            "AND t.fecha >= :fechaLimite " +
            "AND t.idEstadoConsulta != 3 " +
            "AND i.fechaHoraBorradoSuave IS NULL")
    List<EstudioHistorialDTO> findEstudiosHistorialPorPaciente(
            @Param("idPaciente") Long idPaciente,
            @Param("fechaLimite") LocalDate fechaLimite);
}