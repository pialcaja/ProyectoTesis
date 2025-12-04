package com.ecapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecargaRequest {
    
    private double monto;
    private Long idMedioPago;
}
