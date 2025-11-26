package com.ecapi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecapi.service.ParaderoService;

@RestController
@RequestMapping("/api/paradero")
@CrossOrigin(origins = "http://localhost:4200")
public class ParaderoController {

    @Autowired
    private ParaderoService service;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> listar() {
        return service.listar();
    }
}

