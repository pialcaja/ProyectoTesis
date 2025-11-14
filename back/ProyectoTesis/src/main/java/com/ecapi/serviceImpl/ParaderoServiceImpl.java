package com.ecapi.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecapi.dto.ParaderoRequestDTO;
import com.ecapi.model.Paradero;
import com.ecapi.repository.ParaderoRepository;
import com.ecapi.service.ParaderoService;

@Service
public class ParaderoServiceImpl implements ParaderoService {

	@Autowired
	private ParaderoRepository paraderoRepo;
	
	@Override
	public ResponseEntity<Map<String, Object>> listar(int page, int size, String sortBy, String order, String filtro) {
		Map<String, Object> respuesta = new HashMap<>();
		try {
			Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) 
        			? Sort.by(sortBy).ascending() 
        			: Sort.by(sortBy).descending();

			Pageable pageable = PageRequest.of(page, size, sort);
			
			Page<Paradero> paraderos;
			
			if (filtro != null && !filtro.trim().isEmpty()) {
				paraderos = paraderoRepo.buscarPorFiltro(filtro.trim(), pageable);
			} else {
				paraderos = paraderoRepo.findAll(pageable);
			}
			
			respuesta.put("paraderos", paraderos);
			respuesta.put("currentPage", paraderos.getNumber());
			respuesta.put("totalItems", paraderos.getTotalElements());
			respuesta.put("totalPages", paraderos.getTotalPages());
			
			return ResponseEntity.status(HttpStatus.OK).body(respuesta);
		} catch (Exception e) {
			respuesta.put("mensaje", "Error al listar paraderos: " + e.getMessage());
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> registrar(ParaderoRequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Map<String, Object>> obtenerPorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Map<String, Object>> actualizar(Long id, ParaderoRequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Map<String, Object>> eliminar(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Map<String, Object>> recuperar(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
