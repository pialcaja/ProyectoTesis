package com.ecapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.Transaccion;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

}
