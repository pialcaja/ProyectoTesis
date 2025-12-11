package com.ecapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ecapi.dto.MedioPagoDTO;
import com.ecapi.dto.RecargaRequest;
import com.ecapi.dto.RecargaResponseDTO;
import com.ecapi.dto.TarjetaBusDTO;

public interface TarjetaBusService {

    TarjetaBusDTO consultarSaldo(String email);
    
    List<MedioPagoDTO> obtenerMediosPago(String email);
    
    RecargaResponseDTO recargarTarjeta(String email, RecargaRequest request);
    
    ResponseEntity<Map<String, Object>> listarTransacciones(int page, int size, String estado, LocalDate fechaDesde, LocalDate fechaHasta);
}
