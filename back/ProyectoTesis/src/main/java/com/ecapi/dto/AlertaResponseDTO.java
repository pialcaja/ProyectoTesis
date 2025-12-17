package com.ecapi.dto;

import java.time.LocalDateTime;

import com.ecapi.model.EstadoAlerta;
import com.ecapi.model.SentidoRuta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertaResponseDTO {

    private Long id;
    private String rutaNombre;
    private String paraderoNombre;
    private SentidoRuta sentido;
    private Integer minutosAntes;
    private EstadoAlerta estado;
    private LocalDateTime fechaEnvio;
}

