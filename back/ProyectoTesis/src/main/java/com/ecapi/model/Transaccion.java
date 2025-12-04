package com.ecapi.model;

import java.time.LocalDateTime;

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
@Table(name = "tb_transaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 50)
	private String tipo; // RECARGA
	
	@Column(nullable = false)
	private double monto;
	
	@Column(nullable = false)
	private LocalDateTime fecha;
	
	@Column(nullable = false, length = 20)
	private String estado; // EXITOSA, RECHAZADA, ERROR
	
	@ManyToOne
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "id_tarjeta", nullable = false)
	private TarjetaBus tarjeta;
	
	@ManyToOne
	@JoinColumn(name = "id_medio_pago", nullable = true)
	private MedioPago medioPago;
}
