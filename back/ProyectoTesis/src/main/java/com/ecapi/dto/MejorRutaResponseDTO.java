package com.ecapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MejorRutaResponseDTO {

	private String mensaje;
	private ParaderoDTO origen;
	private ParaderoDTO destino;
	private Long rutaId;
	private String rutaNombre;
	private String sentido;
	private Integer distanciaOrden;

	// Para trazar la ruta en el mapa private
	List<ParaderoDTO> paraderosRuta;
}

