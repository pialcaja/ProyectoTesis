package com.ecapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.RutaParadero;

@Repository
public interface RutaParaderoRepository extends JpaRepository<RutaParadero, Long> {

    // Todos los paraderos de una ruta en un sentido
    List<RutaParadero> findByRutaIdAndSentidoOrderByOrdenAsc(Long rutaId, int sentido);

    // Buscar por un paradero en todas las rutas
    List<RutaParadero> findByParaderoId(Long paraderoId);

}

