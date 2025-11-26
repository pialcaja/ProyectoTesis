package com.ecapi.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecapi.model.Paradero;

@Repository
public interface ParaderoRepository extends JpaRepository<Paradero, Long> {

	@Query(value = """
			SELECT *, 
			       (6371 * ACOS(
			           COS(RADIANS(:lat)) * COS(RADIANS(lat)) *
			           COS(RADIANS(lng) - RADIANS(:lng)) +
			           SIN(RADIANS(:lat)) * SIN(RADIANS(lat))
			       )) AS distancia
			FROM tb_paradero
			HAVING distancia <= :radioKm
			ORDER BY distancia ASC
			LIMIT 1
			""", nativeQuery = true)
			Paradero findParaderoCercanoEnRango(
			        @Param("lat") BigDecimal lat,
			        @Param("lng") BigDecimal lng,
			        @Param("radioKm") double radioKm
			);

}


