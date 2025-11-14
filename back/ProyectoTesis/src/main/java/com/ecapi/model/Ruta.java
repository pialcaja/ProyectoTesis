package com.ecapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_ruta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 50)
	private String nombre;
	
	@Column(nullable = false)
	private int estado;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_linea", nullable = false)
	private Linea linea;
}
