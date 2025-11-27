package com.ecapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_tarjeta_bus")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaBus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 16)
	private String numTarjeta;
	
	@Column(nullable = false)
	private double saldo;
	
	@Column(nullable = false)
	private int estado;
}
