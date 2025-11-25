package com.ecapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.RutaParadero;

@Repository
public interface RutaParaderoRepository extends JpaRepository<RutaParadero, Long> {

    // TODOS LOS PARADEROS DE UNA RUTA EN UN SENTIDO
    List<RutaParadero> findByRutaIdAndSentidoOrderByOrdenAsc(Long rutaId, int sentido);

    // BUSCAR POR UN PARADERO EN TODAS LAS RUTAS
    List<RutaParadero> findByParaderoId(Long paraderoId);
}
