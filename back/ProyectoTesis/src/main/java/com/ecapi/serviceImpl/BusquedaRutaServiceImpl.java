package com.ecapi.serviceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecapi.dto.MejorRutaResponseDTO;
import com.ecapi.dto.ParaderoDTO;
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

    private final double RADIO_KM = 0.5; // 500 metros

    @Override
    public ResponseEntity<MejorRutaResponseDTO> buscarMejorRuta(
            BigDecimal latOrigen, BigDecimal lngOrigen,
            BigDecimal latDestino, BigDecimal lngDestino) {

        MejorRutaResponseDTO dto = new MejorRutaResponseDTO();

        double latO = latOrigen.doubleValue();
        double lngO = lngOrigen.doubleValue();
        double latD = latDestino.doubleValue();
        double lngD = lngDestino.doubleValue();

        // 1. Buscar paraderos cercanos
        List<Paradero> candidatosOrigen = listarParaderosEnRango(latO, lngO, RADIO_KM);
        List<Paradero> candidatosDestino = listarParaderosEnRango(latD, lngD, RADIO_KM);

        if (candidatosOrigen.isEmpty() && candidatosDestino.isEmpty()) {
            dto.setMensaje("No hay paraderos cercanos ni al origen ni al destino (fuera del área de servicio).");
            return ResponseEntity.ok(dto);
        }

        if (candidatosOrigen.isEmpty()) {
            dto.setMensaje("No hay paraderos cercanos al ORIGEN (máx. 500 metros).");
            return ResponseEntity.ok(dto);
        }

        if (candidatosDestino.isEmpty()) {
            dto.setMensaje("No hay paraderos cercanos al DESTINO (máx. 500 metros).");
            return ResponseEntity.ok(dto);
        }

        // 2. Comparar TODAS las combinaciones posibles
        Long mejorRutaId = null;
        int menorDistancia = Integer.MAX_VALUE;
        String mejorSentido = null;
        Paradero mejorParaderoOrigen = null;
        Paradero mejorParaderoDestino = null;

        for (Paradero pOrigen : candidatosOrigen) {
            List<RutaParadero> rutasOrigen = rutaParaderoRepo.findByParaderoId(pOrigen.getId());

            for (Paradero pDestino : candidatosDestino) {
                List<RutaParadero> rutasDestino = rutaParaderoRepo.findByParaderoId(pDestino.getId());

                for (RutaParadero r1 : rutasOrigen) {
                    for (RutaParadero r2 : rutasDestino) {

                        if (!Objects.equals(r1.getRuta().getId(), r2.getRuta().getId())) continue;

                        Long rutaId = r1.getRuta().getId();

                        ResultadoEvaluacion res = evaluarSentidos(rutaId, pOrigen, pDestino);

                        if (res != null && res.distancia < menorDistancia) {
                            menorDistancia = res.distancia;
                            mejorRutaId = rutaId;
                            mejorSentido = res.sentido;
                            mejorParaderoOrigen = pOrigen;
                            mejorParaderoDestino = pDestino;
                        }
                    }
                }
            }
        }

        if (mejorRutaId == null) {
            dto.setMensaje("No existe un sentido válido para conectar origen y destino en una misma ruta.");
            dto.setOrigen(mapParadero(candidatosOrigen.get(0)));
            dto.setDestino(mapParadero(candidatosDestino.get(0)));
            return ResponseEntity.ok(dto);
        }

        // 3. Construir respuesta detallada
        Ruta mejorRuta = rutaRepo.findById(mejorRutaId).orElse(null);

        dto.setRutaId(mejorRuta.getId());
        dto.setRutaNombre(mejorRuta.getNombre());
        dto.setSentido(mejorSentido);
        dto.setDistanciaOrden(menorDistancia);

        dto.setOrigen(mapParadero(mejorParaderoOrigen));
        dto.setDestino(mapParadero(mejorParaderoDestino));

        // 4. Enviar paraderos ordenados de la ruta seleccionada
        dto.setParaderosRuta(
        	    mapRutaParaderos(
        	        mejorRutaId,
        	        mejorSentido,
        	        mejorParaderoOrigen.getId(),
        	        mejorParaderoDestino.getId()
        	    )
        	);

        dto.setMensaje("Ruta óptima encontrada.");

        return ResponseEntity.ok(dto);
    }

    // ----------------------------------------------------------------------
    // MÉTODOS PRIVADOS
    // ----------------------------------------------------------------------

    private List<Paradero> listarParaderosEnRango(double lat, double lng, double radioKm) {
        List<Paradero> todos = paraderoRepo.findAll();
        List<Paradero> resultado = new ArrayList<>();

        for (Paradero p : todos) {
            double distancia = calcularDistanciaKm(lat, lng,
                    p.getLat().doubleValue(), p.getLng().doubleValue());

            if (distancia <= radioKm) {
                resultado.add(p);
            }
        }

        // Ordenar por distancia
        resultado.sort(Comparator.comparingDouble(
                p -> calcularDistanciaKm(lat, lng, p.getLat().doubleValue(), p.getLng().doubleValue())
        ));

        return resultado;
    }

    private ResultadoEvaluacion evaluarSentidos(Long rutaId, Paradero origen, Paradero destino) {
        ResultadoEvaluacion ida = evaluarSentido(rutaId, origen, destino, 1);
        ResultadoEvaluacion retorno = evaluarSentido(rutaId, origen, destino, 2);

        if (ida == null) return retorno;
        if (retorno == null) return ida;

        return (ida.distancia < retorno.distancia) ? ida : retorno;
    }

    private ResultadoEvaluacion evaluarSentido(Long rutaId, Paradero origen, Paradero destino, int sentido) {

        List<RutaParadero> lista = rutaParaderoRepo.findByRutaIdAndSentidoOrderByOrdenAsc(rutaId, sentido);

        Integer ordenO = null;
        Integer ordenD = null;

        for (RutaParadero rp : lista) {
            if (rp.getParadero().getId().equals(origen.getId())) ordenO = rp.getOrden();
            if (rp.getParadero().getId().equals(destino.getId())) ordenD = rp.getOrden();
        }

        // El destino debe estar más adelante en la ruta
        if (ordenO == null || ordenD == null || ordenO >= ordenD) {
            return null;
        }

        ResultadoEvaluacion res = new ResultadoEvaluacion();
        res.distancia = ordenD - ordenO;
        res.sentido = (sentido == 1) ? "IDA" : "RETORNO";
        return res;
    }

    private double calcularDistanciaKm(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    // ---- Clase interna auxiliar ---
    private static class ResultadoEvaluacion {
        int distancia;
        String sentido;
    }

    private ParaderoDTO mapParadero(Paradero p) {
        return new ParaderoDTO(
                p.getId(),
                p.getNombre(),
                p.getLat(),
                p.getLng()
        );
    }

    private List<ParaderoDTO> mapRutaParaderos(Long rutaId, String sentido, Long origenId, Long destinoId) {

        int sentidoInt = sentido.equals("IDA") ? 1 : 2;

        List<RutaParadero> lista = rutaParaderoRepo
                .findByRutaIdAndSentidoOrderByOrdenAsc(rutaId, sentidoInt);

        // Encontrar posiciones
        int idxO = -1;
        int idxD = -1;

        for (int i = 0; i < lista.size(); i++) {
            Long pid = lista.get(i).getParadero().getId();
            if (pid.equals(origenId)) idxO = i;
            if (pid.equals(destinoId)) idxD = i;
        }

        // Crear subtramo
        if (idxO == -1 || idxD == -1 || idxO >= idxD) {
            return Collections.emptyList();
        }

        return lista.subList(idxO, idxD + 1).stream()
                .map(rp -> mapParadero(rp.getParadero()))
                .collect(Collectors.toList());
    }

}
