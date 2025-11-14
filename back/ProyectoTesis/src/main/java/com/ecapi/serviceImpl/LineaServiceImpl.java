package com.ecapi.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecapi.dto.LineaRequestDTO;
import com.ecapi.model.Linea;
import com.ecapi.repository.LineaRepository;
import com.ecapi.service.LineaService;
import com.ecapi.utils.TextoUtils;

@Service
public class LineaServiceImpl implements LineaService {

	@Autowired
	private LineaRepository lineaRepo;
	
	@Override
	public ResponseEntity<Map<String, Object>> listar() {
		Map<String, Object> respuesta = new HashMap<>();
		List<Linea> lineas = lineaRepo.findAllByEstado(1);
		
		if (!lineas.isEmpty()) {
			respuesta.put("mensaje", "Lineas disponibles encontradas");
			respuesta.put("lineas", lineas);
			
			return ResponseEntity.status(HttpStatus.OK).body(respuesta);
		} else {
			respuesta.put("mensaje", "No se encontraron lineas disponibles");
			
			return ResponseEntity.status(HttpStatus.OK).body(respuesta);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Map<String, Object>> registrar(LineaRequestDTO dto) {
		Map<String, Object> respuesta = new HashMap<>();
		try {
			validarDuplicadoNombre(dto.getNombre(), null);

			Linea linea = new Linea();
			linea.setNombre(TextoUtils.formatoPrimeraLetraMayuscula(dto.getNombre()));
			linea.setEstado(1);

			lineaRepo.save(linea);
			
			respuesta.put("mensaje", "Línea registrada correctamente");
			respuesta.put("linea", linea);
			
			return ResponseEntity.status(HttpStatus.OK).body(respuesta);
		} catch (Exception e) {
			respuesta.put("mensaje", "Error al registrar línea: " + e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> obtenerPorId(Long id) {
		Map<String, Object> respuesta = new HashMap<>();
		Linea linea = lineaRepo.findById(id).orElseThrow(()
						-> new RuntimeException("Línea no encontrada"));
		
		respuesta.put("Linea", linea);
		
		return ResponseEntity.status(HttpStatus.OK).body(respuesta);
	}

	@Override
	@Transactional
	public ResponseEntity<Map<String, Object>> actualizar(Long id, LineaRequestDTO dto) {
		Map<String, Object> respuesta = new HashMap<>();
		try {
			Linea linea = lineaRepo.findById(id).orElseThrow(()
					-> new RuntimeException("Línea no encontrada"));
			
			validarDuplicadoNombre(dto.getNombre(), id);
			
			linea.setNombre(TextoUtils.formatoPrimeraLetraMayuscula(dto.getNombre()));
			
			lineaRepo.save(linea);
			
			respuesta.put("mensaje", "Línea actualizada correctamente");
			respuesta.put("linea", linea);
			
			return ResponseEntity.status(HttpStatus.OK).body(respuesta);
		} catch (Exception e) {
			respuesta.put("mensaje", "Error al actualizar línea: " + e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> eliminar(Long id) {
		Map<String, Object> respuesta = new HashMap<>();
		try {
			Optional<Linea> lineaOpt = lineaRepo.findById(id);
			
			if (lineaOpt.isEmpty()) {
				respuesta.put("mensaje", "Linea no encontrada");
				
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
			}
			
			Linea linea = lineaOpt.get();
			
			if (linea.getEstado() == 2) {
				respuesta.put("mensaje", "Línea actualmente eliminada");
				
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
			}
			
			linea.setEstado(2);
			
			lineaRepo.save(linea);
			
			respuesta.put("mensaje", "Línea eliminada correctamente");
			
			return ResponseEntity.status(HttpStatus.OK).body(respuesta);
		} catch (Exception e) {
			respuesta.put("mensaje", "Error al eliminar línea: " + e.getMessage());
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> recuperar(Long id) {
		Map<String, Object> respuesta = new HashMap<>();
		try {
			Optional<Linea> lineaOpt = lineaRepo.findById(id);
			
			if (lineaOpt.isEmpty()) {
				respuesta.put("mensaje", "Linea no encontrada");
				
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
			}
			
			Linea linea = lineaOpt.get();
			
			if (linea.getEstado() == 1) {
				respuesta.put("mensaje", "Línea actualmente recuperada");
				
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
			}
			
			linea.setEstado(1);
			
			lineaRepo.save(linea);
			
			respuesta.put("mensaje", "Línea recuperada correctamente");
			
			return ResponseEntity.status(HttpStatus.OK).body(respuesta);
		} catch (Exception e) {
			respuesta.put("mensaje", "Error al recuperar línea: " + e.getMessage());
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
		}
	}
	
	private void validarDuplicadoNombre(String nombre, Long idExcluido) {
		boolean existeNombre = (idExcluido == null) ? lineaRepo.existsByNombreIgnoreCase(nombre)
				: lineaRepo.existsByNombreIgnoreCaseAndIdNot(nombre, idExcluido);
		
		if (existeNombre) {
			throw new RuntimeException("Nombre de linea ya registrado");
		}
	}

}
