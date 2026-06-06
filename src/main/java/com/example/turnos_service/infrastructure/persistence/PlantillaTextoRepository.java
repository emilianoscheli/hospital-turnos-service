package com.example.turnos_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlantillaTextoRepository extends JpaRepository<PlantillaTextoEntity, Long> {
    List<PlantillaTextoEntity> findByIdMetodoEstudioAndIdGrupoPlantilla(Integer idMetodo, Integer idGrupo);
}