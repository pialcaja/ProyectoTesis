package com.ecapi.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecapi.dto.MedioPagoDTO;
import com.ecapi.dto.RecargaRequest;
import com.ecapi.dto.RecargaResponseDTO;
import com.ecapi.dto.TarjetaBusDTO;
import com.ecapi.service.TarjetaBusService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/tarjeta")
@RequiredArgsConstructor
public class TarjetaBusController {

    private final TarjetaBusService tarjetaBusService;

    @GetMapping("/saldo")
    public ResponseEntity<?> consultarSaldo(Authentication authentication) {
        try {
            String email = authentication.getName();
            TarjetaBusDTO tarjeta = tarjetaBusService.consultarSaldo(email);
            return ResponseEntity.ok(tarjeta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/medios-pago")
    public ResponseEntity<?> obtenerMediosPago(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<MedioPagoDTO> mediosPago = tarjetaBusService.obtenerMediosPago(email);
            return ResponseEntity.ok(mediosPago);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/recargar")
    public ResponseEntity<?> recargarTarjeta(@RequestBody RecargaRequest request, 
                                             Authentication authentication) {
        try {
            String email = authentication.getName();
            RecargaResponseDTO response = tarjetaBusService.recargarTarjeta(email, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/transacciones")
    public ResponseEntity<Map<String, Object>> listarTransacciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta
    ) {
        return tarjetaBusService.listarTransacciones(page, size, estado, fechaDesde, fechaHasta);
    }

    
}
