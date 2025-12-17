package com.ecapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecapi.dto.AlertaRequestDTO;
import com.ecapi.dto.AlertaResponseDTO;
import com.ecapi.model.EstadoAlerta;
import com.ecapi.service.AlertaNotificacionService;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "http://localhost:4200")
public class AlertaNotificacionController {

	@Autowired
	private AlertaNotificacionService alertaService;

	@PostMapping
	public ResponseEntity<AlertaResponseDTO> crearAlerta(@RequestBody AlertaRequestDTO request) {
		return ResponseEntity.ok(alertaService.crearAlerta(request));
	}

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<List<AlertaResponseDTO>> listarAlertasUsuario(@PathVariable Long usuarioId) {
		return ResponseEntity.ok(alertaService.listarAlertasPorUsuario(usuarioId));
	}

	@PatchMapping("/{id}/estado")
	public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam EstadoAlerta estado) {
		alertaService.cambiarEstado(id, estado);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/usuario/{id}/pendientes")
	public ResponseEntity<List<AlertaResponseDTO>> listarPendientes(@PathVariable Long id) {
	    return ResponseEntity.ok(alertaService.listarAlertasPendientesPorUsuario(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
	    alertaService.eliminarAlerta(id);
	    return ResponseEntity.noContent().build();
	}
}
