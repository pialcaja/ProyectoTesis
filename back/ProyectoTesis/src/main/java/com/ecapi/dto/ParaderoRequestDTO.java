package com.ecapi.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ParaderoRequestDTO {

	private String nombre;
	
	private BigDecimal lat;
	
	private BigDecimal lng;
}
