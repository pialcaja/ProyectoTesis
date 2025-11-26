package com.ecapi.service;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;

import com.ecapi.dto.MejorRutaResponseDTO;

public interface BusquedaRutaService {

    public ResponseEntity<MejorRutaResponseDTO> buscarMejorRuta(
            BigDecimal latOrigen,
            BigDecimal lngOrigen,
            BigDecimal latDestino,
            BigDecimal lngDestino);
}
