package com.ecapi.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecapi.model.Paradero;

@Repository
public interface ParaderoRepository extends JpaRepository<Paradero, Long> {

	Optional<Paradero> findByNombre(String nombre);
	
    @Query(value = """
            SELECT *, 
            (6371 * ACOS(
                 COS(RADIANS(:lat)) 
               * COS(RADIANS(lat)) 
               * COS(RADIANS(lng) - RADIANS(:lng)) 
               + SIN(RADIANS(:lat)) 
               * SIN(RADIANS(lat))
            )) AS distance
            FROM tb_paradero
            ORDER BY distance ASC
            LIMIT 1
            """, nativeQuery = true)
        Paradero findParaderoCercano(@Param("lat") BigDecimal lat,
                                     @Param("lng") BigDecimal lng);
}
