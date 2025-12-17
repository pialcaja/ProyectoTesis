package com.ecapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecapi.model.AlertaNotificacion;
import com.ecapi.model.EstadoAlerta;
import com.ecapi.model.SentidoRuta;

@Repository
public interface AlertaNotificacionRepository extends JpaRepository<AlertaNotificacion, Long> {

	List<AlertaNotificacion> findByUsuarioId(Long usuarioId);

	List<AlertaNotificacion> findByUsuarioIdAndEstado(Long usuarioId, EstadoAlerta estado);

	List<AlertaNotificacion> findByRutaIdAndSentidoAndEstado(Long rutaId, SentidoRuta sentido, EstadoAlerta estado);

	List<AlertaNotificacion> findByRutaIdAndSentidoAndParaderoIdAndEstado(Long rutaId, SentidoRuta sentido,
			Long paraderoId, EstadoAlerta estado);

	List<AlertaNotificacion> findByEstado(EstadoAlerta estado);

	@Query("""
			    SELECT a
			    FROM AlertaNotificacion a
			    WHERE a.estado = com.ecapi.model.EstadoAlerta.PENDIENTE
			    AND a.ruta.id = :rutaId
			    AND a.sentido = :sentido
			""")
	List<AlertaNotificacion> buscarAlertasPendientes(@Param("rutaId") Long rutaId,
			@Param("sentido") SentidoRuta sentido);
}
