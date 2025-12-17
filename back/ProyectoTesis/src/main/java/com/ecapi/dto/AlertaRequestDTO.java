package com.ecapi.dto;

import com.ecapi.model.SentidoRuta;

import lombok.Data;

@Data
public class AlertaRequestDTO {

    private Long usuarioId;
    private Long rutaId;
    private Long paraderoId;
    private SentidoRuta sentido;
    private Integer minutosAntes;
    private String horaLlegadaAproximada;
}

