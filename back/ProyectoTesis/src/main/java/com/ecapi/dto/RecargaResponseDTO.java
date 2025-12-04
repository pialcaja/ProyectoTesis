package com.ecapi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecargaResponseDTO {
    
    private boolean exitosa;
    private String mensaje;
    private TarjetaBusDTO tarjeta;
    private double montoRecargado;
    private LocalDateTime fechaTransaccion;
}
