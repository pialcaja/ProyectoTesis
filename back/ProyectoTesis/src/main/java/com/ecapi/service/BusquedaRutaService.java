package com.ecapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.ecapi.dto.MejorRutaResponseDTO;
import com.ecapi.dto.ParaderoDTO;
import com.ecapi.model.SentidoRuta;

public interface BusquedaRutaService {

    public ResponseEntity<MejorRutaResponseDTO> buscarMejorRuta(
            BigDecimal latOrigen,
            BigDecimal lngOrigen,
            BigDecimal latDestino,
            BigDecimal lngDestino);
    
    MejorRutaResponseDTO calcularRutaConHorarios(
            Long rutaId,
            SentidoRuta sentido
        );
    
    LocalDateTime calcularHoraLlegadaParadero(
            Long rutaId,
            Long paraderoId,
            SentidoRuta sentido
    );
    
    ParaderoDTO obtenerParaderoDTO(Long rutaId, Long paraderoId, SentidoRuta sentido);
}
