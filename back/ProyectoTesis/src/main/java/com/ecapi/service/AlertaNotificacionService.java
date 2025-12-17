package com.ecapi.service;

import java.util.List;

import com.ecapi.dto.AlertaRequestDTO;
import com.ecapi.dto.AlertaResponseDTO;
import com.ecapi.model.EstadoAlerta;

public interface AlertaNotificacionService {

    void procesarAlertasPendientes();

    AlertaResponseDTO crearAlerta(AlertaRequestDTO request);

    List<AlertaResponseDTO> listarAlertasPorUsuario(Long usuarioId);

    void cambiarEstado(Long alertaId, EstadoAlerta estado);
    
    List<AlertaResponseDTO> listarAlertasPendientesPorUsuario(Long usuarioId);
    
    void eliminarAlerta(Long alertaId);
}
