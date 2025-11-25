package com.ecapi.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecapi.model.Paradero;
import com.ecapi.repository.ParaderoRepository;
import com.ecapi.service.ParaderoService;

@Service
public class ParaderoServiceImpl implements ParaderoService {

	@Autowired
    private ParaderoRepository paraderoRepo;

    @Override
    public ResponseEntity<Map<String, Object>> listar() {
        Map<String, Object> respuesta = new HashMap<>();
        List<Paradero> paraderos = paraderoRepo.findAll();

        respuesta.put("mensaje", paraderos.isEmpty() ? "Paraderos no encontrados" : "Paraderos encontrados");
        respuesta.put("paraderos", paraderos);

        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<Map<String, Object>> registrar() {
        // FALTA AGREGAR LOGICA
        return null;
    }

    @Override
    public ResponseEntity<Map<String, Object>> actualizar() {
        // FALTA AGREGAR LOGICA
        return null;
    }

    @Override
    public ResponseEntity<Map<String, Object>> eliminar() {
        // FALTA AGREGAR LOGICA
        return null;
    }

}
