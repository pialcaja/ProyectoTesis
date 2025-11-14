package com.ecapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecapi.model.Paradero;

@Repository
public interface ParaderoRepository extends JpaRepository<Paradero, Long> {

	@Query("""
			SELECT u FROM Paradero p
			WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :filtro, '%'))
			""")
	Page<Paradero> buscarPorFiltro(@Param("filtro") String filtro, Pageable pageable);
	
	boolean existsByNombreIgnoreCase(String nombre);
	
	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
