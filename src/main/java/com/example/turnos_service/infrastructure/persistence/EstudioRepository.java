package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EstudioRepository extends JpaRepository<EstudioEntity, Long> {
    // Para cuando el frontend quiera filtrar los estudios por "Tipo" (Eco, Rx, TAC)
    List<EstudioEntity> findByIdMetodoEstudio(Integer idMetodoEstudio);
}