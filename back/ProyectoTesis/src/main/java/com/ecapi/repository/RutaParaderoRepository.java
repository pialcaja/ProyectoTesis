package com.ecapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.RutaParadero;

@Repository
public interface RutaParaderoRepository extends JpaRepository<RutaParadero, Long> {

    List<RutaParadero> findByRutaIdAndSentidoOrderByOrdenAsc(Long rutaId, int sentido);

    List<RutaParadero> findByParaderoId(Long paraderoId);

}

