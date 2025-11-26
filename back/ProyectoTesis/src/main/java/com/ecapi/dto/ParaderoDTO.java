package com.ecapi.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParaderoDTO {
	
    private Long id;
    private String nombre;
    private BigDecimal lat;
    private BigDecimal lng;
    
}
