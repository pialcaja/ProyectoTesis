package com.ecapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.Ruta;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

}
