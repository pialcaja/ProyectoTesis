package com.ecapi.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecapi.dto.AlertaRequestDTO;
import com.ecapi.dto.AlertaResponseDTO;
import com.ecapi.model.AlertaNotificacion;
import com.ecapi.model.CanalNotificacion;
import com.ecapi.model.EstadoAlerta;
import com.ecapi.model.Paradero;
import com.ecapi.model.Ruta;
import com.ecapi.model.Usuario;
import com.ecapi.repository.AlertaNotificacionRepository;
import com.ecapi.repository.ParaderoRepository;
import com.ecapi.repository.RutaRepository;
import com.ecapi.repository.UsuarioRepository;
import com.ecapi.service.AlertaNotificacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertaNotificacionServiceImpl implements AlertaNotificacionService {

	private final AlertaNotificacionRepository alertaRepository;
	private final UsuarioRepository usuarioRepository;
	private final RutaRepository rutaRepository;
	private final ParaderoRepository paraderoRepository;

	@Override
	public void procesarAlertasPendientes() {

	    List<AlertaNotificacion> alertas =
	            alertaRepository.findByEstado(EstadoAlerta.PENDIENTE);

	    LocalDateTime ahora = LocalDateTime.now();

	    for (AlertaNotificacion alerta : alertas) {

	        System.out.println("---- ALERTA " + alerta.getId());
	        System.out.println("Ahora:      " + ahora);
	        System.out.println("FechaEnvio: " + alerta.getFechaEnvio());
	        System.out.println("Llegada:    " + alerta.getFechaHoraLlegada());

	        if (ahora.isBefore(alerta.getFechaEnvio())) {
	            System.out.println("⏳ Aún no es hora de notificar");
	            continue;
	        }

	        dispararNotificacion(alerta);
	        alerta.setEstado(EstadoAlerta.ENVIADA);
	        alertaRepository.save(alerta);

	        System.out.println("✅ ALERTA ENVIADA ID=" + alerta.getId());
	    }
	}

	private void dispararNotificacion(AlertaNotificacion alerta) {
	    // enviar websocket / push / toast
	    System.out.println("🔔 Notificando alerta " + alerta.getId());
	}

	@Override
	public AlertaResponseDTO crearAlerta(AlertaRequestDTO request) {

	    System.out.println("🚨 ===== CREANDO ALERTA =====");
	    System.out.println("🕒 Hora actual servidor: " + LocalDateTime.now());

	    Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
	            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

	    Ruta ruta = rutaRepository.findById(request.getRutaId())
	            .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

	    Paradero paradero = paraderoRepository.findById(request.getParaderoId())
	            .orElseThrow(() -> new RuntimeException("Paradero no encontrado"));

	    // --- Usar hora enviada desde el frontend ---
	    if (request.getHoraLlegadaAproximada() == null) {
	        throw new RuntimeException("Debe enviarse la hora aproximada de llegada desde el frontend");
	    }

	    LocalTime horaLlegada = LocalTime.parse(
	            request.getHoraLlegadaAproximada(),
	            DateTimeFormatter.ofPattern("HH:mm")
	    );

	    LocalDateTime fechaHoraLlegada = LocalDateTime.of(LocalDate.now(), horaLlegada);

	    if (fechaHoraLlegada.isBefore(LocalDateTime.now())) {
	        fechaHoraLlegada = fechaHoraLlegada.plusDays(1);
	    }

	    LocalDateTime fechaEnvio = fechaHoraLlegada.minusMinutes(request.getMinutosAntes());

	    System.out.println("🚌 Hora llegada calculada: " + fechaHoraLlegada);
	    System.out.println("⏰ Fecha envío calculada (llegada - minutosAntes): " + fechaEnvio);

	    AlertaNotificacion alerta = new AlertaNotificacion();
	    alerta.setUsuario(usuario);
	    alerta.setRuta(ruta);
	    alerta.setParadero(paradero);
	    alerta.setSentido(request.getSentido());
	    alerta.setMinutosAntes(request.getMinutosAntes());
	    alerta.setCanalNotificacion(CanalNotificacion.WEB);
	    alerta.setFechaHoraLlegada(fechaHoraLlegada);
	    alerta.setFechaEnvio(fechaEnvio);
	    alerta.setEstado(EstadoAlerta.PENDIENTE);

	    alertaRepository.save(alerta);

	    System.out.println("💾 Alerta guardada en BD con ID: " + alerta.getId());
	    System.out.println("🚨 ===========================");

	    return mapToResponse(alerta);
	}

	@Override
	public void cambiarEstado(Long alertaId, EstadoAlerta estado) {

		AlertaNotificacion alerta = alertaRepository.findById(alertaId)
				.orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

		if (alerta.getEstado() == EstadoAlerta.ENVIADA) {
			throw new IllegalStateException("No se puede modificar una alerta ya enviada");
		}

		alerta.setEstado(estado);

		if (estado == EstadoAlerta.CANCELADA) {
			alerta.setFechaEnvio(null);
		}

		alertaRepository.save(alerta);
	}
	
	@Override
	public List<AlertaResponseDTO> listarAlertasPorUsuario(Long usuarioId) {

		return alertaRepository.findByUsuarioId(usuarioId).stream().map(this::mapToResponse).toList();
	}

	private AlertaResponseDTO mapToResponse(AlertaNotificacion alerta) {

		return new AlertaResponseDTO(alerta.getId(), alerta.getRuta().getNombre(), alerta.getParadero().getNombre(),
				alerta.getSentido(), alerta.getMinutosAntes(), alerta.getEstado(), alerta.getFechaEnvio());
	}

	@Override
	public List<AlertaResponseDTO> listarAlertasPendientesPorUsuario(Long usuarioId) {
		return alertaRepository.findByUsuarioIdAndEstado(usuarioId, EstadoAlerta.PENDIENTE).stream()
				.map(this::mapToResponse).toList();
	}

	@Override
	public void eliminarAlerta(Long alertaId) {

		AlertaNotificacion alerta = alertaRepository.findById(alertaId)
				.orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

//		if (alerta.getEstado() == EstadoAlerta.ENVIADA) {
//			throw new IllegalStateException("No se puede eliminar una alerta enviada");
//		}

		alertaRepository.delete(alerta);
	}

}