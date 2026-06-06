package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.turnos_service.application.dto.MedicoInformanteDTO;


import java.util.List;

@Repository
public interface MedicoRepository extends JpaRepository<MedicoEntity, Long> {

    // Reemplaza al 'case "profesionales"' del legacy
    // Busca profesionales por especialidad, que estén activos (borrado lógico) y ordena por apellido
    @Query("SELECT m FROM MedicoEntity m " +
            "JOIN FETCH m.datosPersonales dp " +
            "WHERE m.especialidad.id = :idEspecialidad " +
            "AND m.activo = true " +
            "ORDER BY dp.apellidoPaterno ASC, dp.primerNombre ASC")
    List<MedicoEntity> findByEspecialidadAndActivoTrueOrderByApellido(@Param("idEspecialidad") Long idEspecialidad);

    // Reemplaza la lógica combinada de buscar profesionales que pertenezcan a un Servicio Y una Especialidad
    @Query("SELECT m FROM MedicoEntity m " +
            "JOIN FETCH m.datosPersonales dp " +
            "JOIN m.servicios s " +
            "WHERE m.especialidad.id = :idEspecialidad " +
            "AND s.id = :idServicio " +
            "AND m.activo = true " +
            "ORDER BY dp.apellidoPaterno ASC")
    List<MedicoEntity> findByServicioAndEspecialidadAndActivoTrue(
            @Param("idServicio") Long idServicio,
            @Param("idEspecialidad") Long idEspecialidad);

    // Agregalo en tu MedicoRepository existente
    List<MedicoEntity> findByActivoTrue();

    // IMPORTANTE: Agregá este import arriba de todo en tu MedicoRepository junto con los otros:

    // --- NUEVO MÉTODO PARA EL ENDPOINT DE INFORMES ---
    // Busca los médicos activos que pertenezcan al servicio de Diagnóstico por Imágenes (id = 3)
    @Query("SELECT new com.example.turnos_service.application.dto.MedicoInformanteDTO(m.id, CONCAT(dp.apellidoPaterno, ' ', dp.primerNombre)) " +
            "FROM MedicoEntity m " +
            "JOIN m.datosPersonales dp " +
            "JOIN m.servicios s " +
            "WHERE s.id = 3L AND m.activo = true " +
            "ORDER BY dp.apellidoPaterno ASC, dp.primerNombre ASC")
    List<MedicoInformanteDTO> findMedicosInformantes();
}