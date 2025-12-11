package com.ecapi.model;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

public class TransaccionSpecification {

    public static Specification<Transaccion> estadoEquals(String estado) {
        return (root, query, cb) ->
                estado == null || estado.isEmpty()
                ? cb.conjunction()
                : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Transaccion> fechaDesde(LocalDate fechaDesde) {
        return (root, query, cb) ->
                fechaDesde == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fecha"), fechaDesde);
    }

    public static Specification<Transaccion> fechaHasta(LocalDate fechaHasta) {
        return (root, query, cb) ->
                fechaHasta == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("fecha"), fechaHasta);
    }
}
