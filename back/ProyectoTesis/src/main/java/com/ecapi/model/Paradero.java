package com.ecapi.model;

import java.math.BigDecimal;

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
@Table(name = "tb_paradero")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paradero {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 100)
	private String nombre;
	
	@Column(nullable = false)
	private BigDecimal lat;
	
	@Column(nullable = false)
	private BigDecimal lng;
	
	@Column(nullable = false)
	private int estado;
}
