package com.ecapi.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ecapi.dto.ParaderoRequestDTO;

public interface ParaderoService {

	public ResponseEntity<Map<String, Object>> listar(int page, int size, String sortBy, String order, String filtro);

	public ResponseEntity<Map<String, Object>> registrar(ParaderoRequestDTO dto);

	public ResponseEntity<Map<String, Object>> obtenerPorId(Long id);

	public ResponseEntity<Map<String, Object>> actualizar(Long id, ParaderoRequestDTO dto);

	public ResponseEntity<Map<String, Object>> eliminar(Long id);

	public ResponseEntity<Map<String, Object>> recuperar(Long id);
}
