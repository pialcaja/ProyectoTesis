package com.ecapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedioPagoDTO {
    
    private Long id;
    private String tipo;
    private String descripcion;
    private String numeroEnmascarado;
    private int estado;
}
