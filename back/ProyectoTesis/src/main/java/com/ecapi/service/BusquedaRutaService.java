package com.ecapi.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface BusquedaRutaService {

    public ResponseEntity<Map<String, Object>> buscarMejorRuta(
            BigDecimal latOrigen,
            BigDecimal lngOrigen,
            BigDecimal latDestino,
            BigDecimal lngDestino);
}
