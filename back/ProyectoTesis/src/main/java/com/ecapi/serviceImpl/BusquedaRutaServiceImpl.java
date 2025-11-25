package com.ecapi.serviceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecapi.model.Paradero;
import com.ecapi.model.Ruta;
import com.ecapi.model.RutaParadero;
import com.ecapi.repository.ParaderoRepository;
import com.ecapi.repository.RutaParaderoRepository;
import com.ecapi.repository.RutaRepository;
import com.ecapi.service.BusquedaRutaService;

@Service
public class BusquedaRutaServiceImpl implements BusquedaRutaService {

	@Autowired
	private ParaderoRepository paraderoRepo;
	
    @Autowired
    private RutaRepository rutaRepo;
	
	@Autowired
	private RutaParaderoRepository rutaParaderoRepo;

	@Override
	public ResponseEntity<Map<String, Object>> buscarMejorRuta(BigDecimal latOrigen, BigDecimal lngOrigen,
	        BigDecimal latDestino, BigDecimal lngDestino) {

	    Map<String, Object> response = new HashMap<>();

	    // 1) Paradero más cercano al origen
	    Paradero paraderoOrigen = paraderoRepo.findParaderoCercano(latOrigen, lngOrigen);

	    // 2) Paradero más cercano al destino
	    Paradero paraderoDestino = paraderoRepo.findParaderoCercano(latDestino, lngDestino);

	    if (paraderoOrigen == null || paraderoDestino == null) {
	        response.put("mensaje", "No se encontraron paraderos cercanos.");
	        return ResponseEntity.ok(response);
	    }

	    // 3) Rutas donde aparece cada paradero
	    List<RutaParadero> rutasOrigen = rutaParaderoRepo.findByParaderoId(paraderoOrigen.getId());
	    List<RutaParadero> rutasDestino = rutaParaderoRepo.findByParaderoId(paraderoDestino.getId());

	    // 4) Rutas en común (sin sentido aún)
	    List<Long> rutasEnComun = new ArrayList<>();

	    for (RutaParadero r1 : rutasOrigen) {
	        for (RutaParadero r2 : rutasDestino) {
	            if (Objects.equals(r1.getRuta().getId(), r2.getRuta().getId())) {
	                if (!rutasEnComun.contains(r1.getRuta().getId())) {
	                    rutasEnComun.add(r1.getRuta().getId());
	                }
	            }
	        }
	    }

	    if (rutasEnComun.isEmpty()) {
	        response.put("mensaje", "No existe una ruta que conecte estos paraderos.");
	        response.put("paraderoOrigenDebug", paraderoOrigen);
	        response.put("paraderoDestinoDebug", paraderoDestino);
	        return ResponseEntity.ok(response);
	    }

	    // 5) Evaluar cada ruta y cada sentido
	    Long mejorRutaId = null;
	    Integer mejorSentido = null;
	    int menorDistanciaOrden = Integer.MAX_VALUE;

	    for (Long rutaId : rutasEnComun) {

	        // Evaluar sentidos: 0 = IDA, 1 = VUELTA
	        for (int sentido = 0; sentido <= 1; sentido++) {

	            List<RutaParadero> listaParaderos = 
	                    rutaParaderoRepo.findByRutaIdAndSentidoOrderByOrdenAsc(rutaId, sentido);

	            Integer ordenOrigen = null;
	            Integer ordenDestino = null;

	            for (RutaParadero rp : listaParaderos) {
	                if (rp.getParadero().getId().equals(paraderoOrigen.getId())) {
	                    ordenOrigen = rp.getOrden();
	                }
	                if (rp.getParadero().getId().equals(paraderoDestino.getId())) {
	                    ordenDestino = rp.getOrden();
	                }
	            }

	            // Validar sentido y orden
	            if (ordenOrigen != null && ordenDestino != null && ordenOrigen < ordenDestino) {

	                int distanciaOrden = ordenDestino - ordenOrigen;

	                if (distanciaOrden < menorDistanciaOrden) {
	                    menorDistanciaOrden = distanciaOrden;
	                    mejorRutaId = rutaId;
	                    mejorSentido = sentido;
	                }
	            }
	        }
	    }

	    if (mejorRutaId == null) {
	        response.put("mensaje", "No existe un sentido válido para esta ruta.");
	        response.put("paraderoOrigenDebug", paraderoOrigen);
	        response.put("paraderoDestinoDebug", paraderoDestino);
	        return ResponseEntity.ok(response);
	    }

	    Ruta mejorRuta = rutaRepo.findById(mejorRutaId).orElse(null);

	    response.put("mensaje", "Ruta óptima encontrada.");
	    response.put("origen", paraderoOrigen);
	    response.put("destino", paraderoDestino);
	    response.put("ruta", mejorRuta);
	    response.put("sentido", mejorSentido == 1 ? "IDA" : "VUELTA");
	    response.put("distancia_orden", menorDistanciaOrden);

	    return ResponseEntity.ok(response);
	}
	
}
