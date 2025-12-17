package com.ecapi.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ecapi.dto.MejorRutaResponseDTO;
import com.ecapi.dto.ParaderoDTO;
import com.ecapi.model.Paradero;
import com.ecapi.model.Ruta;
import com.ecapi.model.RutaParadero;
import com.ecapi.model.SentidoRuta;
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

    private final double RADIO_KM = 0.5;
    
    private static final int MINUTOS_POR_PARADERO = 4;

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
        
        LocalTime horaInicio = LocalTime.now();

        // 4. Enviar paraderos ordenados de la ruta seleccionada
        dto.setParaderosRuta(
        	    mapRutaParaderos(
        	        mejorRutaId,
        	        mejorSentido,
        	        mejorParaderoOrigen.getId(),
        	        mejorParaderoDestino.getId(),
        	        horaInicio
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
        res.sentido = (sentido == 1) ? "IDA" : "VUELTA";
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
        return ParaderoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .lat(p.getLat())
                .lng(p.getLng())
                .build();
    }

    private List<ParaderoDTO> mapRutaParaderos(Long rutaId, String sentido, Long origenId, Long destinoId, LocalTime horaInicio) {

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

        List<RutaParadero> subLista = lista.subList(idxO, idxD + 1);

        List<ParaderoDTO> resultado = new ArrayList<>();

        for (int i = 0; i < subLista.size(); i++) {

            Paradero p = subLista.get(i).getParadero();

            LocalTime horaParadero =
            	    horaInicio.plusMinutes((i + 1) * MINUTOS_POR_PARADERO);

            ParaderoDTO dto = new ParaderoDTO(
                    p.getId(),
                    p.getNombre(),
                    p.getLat(),
                    p.getLng(),
                    horaParadero.format(DateTimeFormatter.ofPattern("HH:mm"))
            );

            resultado.add(dto);
        }

        return resultado;

    }

    @Override
    public MejorRutaResponseDTO calcularRutaConHorarios(Long rutaId, SentidoRuta sentido) {

        Ruta ruta = rutaRepo.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        String sentidoTexto = (sentido == SentidoRuta.IDA) ? "IDA" : "VUELTA";

        List<ParaderoDTO> paraderos = mapRutaParaderos(
                rutaId,
                sentidoTexto,
                null,
                null,
                null
        );

        // Hora base
        LocalTime horaInicio = LocalTime.now().plusMinutes(4);

        List<ParaderoDTO> paraderosConHora = new ArrayList<>();

        for (int i = 0; i < paraderos.size(); i++) {
            ParaderoDTO p = paraderos.get(i);

            LocalTime hora = horaInicio.plusMinutes(i * 4);

            p.setHoraLlegadaAproximada(
                hora.format(DateTimeFormatter.ofPattern("HH:mm"))
            );

            paraderosConHora.add(p);
        }

        MejorRutaResponseDTO dto = new MejorRutaResponseDTO();
        dto.setRutaId(ruta.getId());
        dto.setRutaNombre(ruta.getNombre());
        dto.setSentido(sentidoTexto);
        dto.setParaderosRuta(paraderosConHora);

        return dto;
    }


    public LocalDateTime calcularHoraLlegadaParadero(
            Long rutaId,
            Long paraderoId,
            SentidoRuta sentido) {

        String sentidoTexto = (sentido == SentidoRuta.IDA) ? "IDA" : "VUELTA";

        List<RutaParadero> lista = rutaParaderoRepo
            .findByRutaIdAndSentidoOrderByOrdenAsc(
                rutaId,
                sentidoTexto.equals("IDA") ? 1 : 2
            );

        LocalTime horaInicio = LocalTime.now().plusMinutes(4);

        for (int i = 0; i < lista.size(); i++) {
            RutaParadero rp = lista.get(i);

            if (rp.getParadero().getId().equals(paraderoId)) {

                LocalTime hora = horaInicio.plusMinutes((i + 1) * MINUTOS_POR_PARADERO);

                LocalDateTime fechaHora = LocalDateTime.of(LocalDate.now(), hora);

                if (fechaHora.isBefore(LocalDateTime.now())) {
                    fechaHora = fechaHora.plusDays(1);
                }

                return fechaHora;
            }
        }

        return null;
    }
    
    public ParaderoDTO obtenerParaderoDTO(Long rutaId, Long paraderoId, SentidoRuta sentido) {

        String sentidoTexto = (sentido == SentidoRuta.IDA) ? "IDA" : "VUELTA";
        int sentidoInt = sentidoTexto.equals("IDA") ? 1 : 2;

        List<RutaParadero> lista = rutaParaderoRepo.findByRutaIdAndSentidoOrderByOrdenAsc(rutaId, sentidoInt);

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getParadero().getId().equals(paraderoId)) {
                Paradero p = lista.get(i).getParadero();
                // Calculamos la hora aproximada como en mapRutaParaderos
                LocalTime horaLlegada = LocalTime.now().plusMinutes((i + 1) * MINUTOS_POR_PARADERO);
                return new ParaderoDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getLat(),
                        p.getLng(),
                        horaLlegada.format(DateTimeFormatter.ofPattern("HH:mm"))
                );
            }
        }

        return null; // no encontrado
    }

}
