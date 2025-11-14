package com.ecapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecapi.model.Paradero;

@Repository
public interface ParaderoRepository extends JpaRepository<Paradero, Long> {

}
