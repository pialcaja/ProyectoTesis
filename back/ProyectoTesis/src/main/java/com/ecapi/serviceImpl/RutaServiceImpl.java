package com.ecapi.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecapi.model.Ruta;
import com.ecapi.repository.RutaRepository;
import com.ecapi.service.RutaService;

@Service
public class RutaServiceImpl implements RutaService {

	@Autowired
	private RutaRepository rutaRepo;

	@Override
	public ResponseEntity<Map<String, Object>> listar() {
	    Map<String, Object> respuesta = new HashMap<>();
	    List<Ruta> rutas = rutaRepo.findAll();
	    
	    respuesta.put("mensaje", rutas.isEmpty() ? "Rutas no encontradas" : "Rutas encontradas");
	    respuesta.put("rutas", rutas);
	    
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
