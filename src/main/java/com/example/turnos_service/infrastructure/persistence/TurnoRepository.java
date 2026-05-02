package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;

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
}