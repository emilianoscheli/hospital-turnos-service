package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InformeEstudioRepository extends JpaRepository<InformeEstudioEntity, Long> {

    // Busca si hay un informe activo asociado a ese turno
    Optional<InformeEstudioEntity> findByTurnoIdAndFechaHoraBorradoSuaveIsNull(Long idTurno);
}