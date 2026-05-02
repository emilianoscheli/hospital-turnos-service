package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<ServicioEntity, Long> {
    // Hereda findAll() automáticamente
}