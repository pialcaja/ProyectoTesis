package com.ecapi.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface ParaderoService {

    public ResponseEntity<Map<String, Object>> listar();

    public ResponseEntity<Map<String, Object>> registrar();

    public ResponseEntity<Map<String, Object>> actualizar();

    public ResponseEntity<Map<String, Object>> eliminar();
}
