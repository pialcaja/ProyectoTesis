package com.ecapi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TarjetaBusDTO {
    
    private Long id;
    private String numTarjeta;
    private double saldo;
    private int estado;
    private LocalDateTime fechaActualizacion;
}
