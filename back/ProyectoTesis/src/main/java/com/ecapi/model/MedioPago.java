package com.ecapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tb_medio_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedioPago {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 50)
	private String tipo; // TARJETA, YAPE, PLIN
	
	@Column(nullable = false, length = 100)
	private String descripcion;
	
	@Column(nullable = false, length = 20)
	private String numeroEnmascarado; // **** **** **** 1234
	
	@Column(nullable = false)
	private int estado;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;
}
