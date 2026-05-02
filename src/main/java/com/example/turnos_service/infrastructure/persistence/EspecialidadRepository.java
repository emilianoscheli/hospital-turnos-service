package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspecialidadRepository extends JpaRepository<EspecialidadEntity, Long> {

    // Equivale al 'case "especialidades"' de tu legacy.
    // Busca especialidades que tengan médicos activos asignados a un servicio específico.
    @Query("SELECT DISTINCT e FROM MedicoEntity m " +
            "JOIN m.especialidad e " +
            "JOIN m.servicios s " +
            "WHERE s.id = :idServicio AND m.activo = true " +
            "ORDER BY e.especialidad ASC")
    List<EspecialidadEntity> findEspecialidadesByServicioActivo(@Param("idServicio") Long idServicio);
}