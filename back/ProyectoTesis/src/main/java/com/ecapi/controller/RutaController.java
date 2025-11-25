package com.ecapi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ecapi.service.RutaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/ruta")
public class RutaController {

	@Autowired
	private RutaService service;
	
	@GetMapping()
	public ResponseEntity<Map<String, Object>> listar() {
		return service.listar();
	}
	
}
