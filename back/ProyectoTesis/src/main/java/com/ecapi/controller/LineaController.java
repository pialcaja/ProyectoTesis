package com.ecapi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecapi.dto.LineaRequestDTO;
import com.ecapi.service.LineaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/linea")
public class LineaController {

	 @Autowired
	 private LineaService lineaService;
	 
	 @GetMapping("/listar")
	 public ResponseEntity<Map<String, Object>> listar() {
	 	return lineaService.listar();
	 }
	 
	 @PostMapping("/registrar")
	 public ResponseEntity<Map<String, Object>> registrar(@RequestBody LineaRequestDTO dto) {
	 	return lineaService.registrar(dto);
	 }
	 
	 @GetMapping("/obtener/{id}")
	 public ResponseEntity<Map<String, Object>> obtener(@PathVariable Long id) {
	 	return lineaService.obtenerPorId(id);
	 }
	 
	 @PutMapping("/actualizar/{id}")
	 public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id, @RequestBody LineaRequestDTO dto) {
	 	return lineaService.actualizar(id, dto);
	 }
	 
	 @PutMapping("/eliminar/{id}")
	 public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
	 	return lineaService.eliminar(id);
	 }
	 
	 @PutMapping("/recuperar/{id}")
	 public ResponseEntity<Map<String, Object>> recuperar(@PathVariable Long id) {
	 	return lineaService.recuperar(id);
	 }
}
