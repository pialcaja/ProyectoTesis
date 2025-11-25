package com.ecapi.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecapi.service.BusquedaRutaService;

@RestController
@RequestMapping("/api/busqueda")
public class BusquedaRutaController {

	@Autowired
	private BusquedaRutaService service;
	
    @GetMapping("/mejor-ruta")
    public ResponseEntity<Map<String, Object>> mejorRuta(
            @RequestParam BigDecimal latOrigen,
            @RequestParam BigDecimal lngOrigen,
            @RequestParam BigDecimal latDestino,
            @RequestParam BigDecimal lngDestino) {

        return service.buscarMejorRuta(latOrigen, lngOrigen, latDestino, lngDestino);
    }
}
