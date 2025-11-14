package com.ecapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.Linea;

@Repository
public interface LineaRepository extends JpaRepository<Linea, Long> {

	List<Linea> findAllByEstado(int estado);
	
	boolean existsByNombreIgnoreCase(String nombre);
	
	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
