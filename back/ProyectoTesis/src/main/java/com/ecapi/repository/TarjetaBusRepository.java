package com.ecapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.TarjetaBus;

@Repository
public interface TarjetaBusRepository extends JpaRepository<TarjetaBus, Long> {

    Optional<TarjetaBus> findByNumTarjeta(String numTarjeta);
    
    boolean existsByNumTarjeta(String numTarjeta);
}
