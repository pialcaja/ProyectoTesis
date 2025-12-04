package com.ecapi.service;

import java.util.List;

import com.ecapi.dto.MedioPagoDTO;
import com.ecapi.dto.RecargaRequest;
import com.ecapi.dto.RecargaResponseDTO;
import com.ecapi.dto.TarjetaBusDTO;

public interface TarjetaBusService {

    TarjetaBusDTO consultarSaldo(String email);
    
    List<MedioPagoDTO> obtenerMediosPago(String email);
    
    RecargaResponseDTO recargarTarjeta(String email, RecargaRequest request);
}
