package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoEstudioRepository extends JpaRepository<MetodoEstudioEntity, Integer> {

    // Hace un JOIN directo con la tabla intermedia que me pasaste ayer
    @Query(value = "SELECT m.* FROM tmetodosestudio m " +
            "INNER JOIN tmetodosestudioporespecialidad me ON m.id = me.idmetodoestudio " +
            "WHERE me.idespecialidad = :idEspecialidad", nativeQuery = true)
    List<MetodoEstudioEntity> findByEspecialidadNative(@Param("idEspecialidad") Integer idEspecialidad);
}