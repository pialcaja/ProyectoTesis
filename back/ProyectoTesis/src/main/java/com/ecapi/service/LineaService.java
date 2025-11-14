package com.ecapi.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ecapi.dto.LineaRequestDTO;

public interface LineaService {

	public ResponseEntity<Map<String, Object>> listar();
	
	public ResponseEntity<Map<String, Object>> registrar(LineaRequestDTO dto);
	
	public ResponseEntity<Map<String, Object>> obtenerPorId(Long id);
	
	public ResponseEntity<Map<String, Object>> actualizar(Long id, LineaRequestDTO dto);
	
	public ResponseEntity<Map<String, Object>> eliminar(Long id);
	
	public ResponseEntity<Map<String, Object>> recuperar(Long id);
}
