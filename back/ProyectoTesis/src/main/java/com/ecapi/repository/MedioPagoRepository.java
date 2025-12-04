package com.ecapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.MedioPago;
import com.ecapi.model.Usuario;

@Repository
public interface MedioPagoRepository extends JpaRepository<MedioPago, Long> {

    List<MedioPago> findByUsuarioAndEstado(Usuario usuario, int estado);
}
