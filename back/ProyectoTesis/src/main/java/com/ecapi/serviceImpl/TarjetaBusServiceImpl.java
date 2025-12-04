package com.ecapi.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecapi.dto.MedioPagoDTO;
import com.ecapi.dto.RecargaRequest;
import com.ecapi.dto.RecargaResponseDTO;
import com.ecapi.dto.TarjetaBusDTO;
import com.ecapi.model.MedioPago;
import com.ecapi.model.TarjetaBus;
import com.ecapi.model.Transaccion;
import com.ecapi.model.Usuario;
import com.ecapi.repository.MedioPagoRepository;
import com.ecapi.repository.TarjetaBusRepository;
import com.ecapi.repository.TransaccionRepository;
import com.ecapi.repository.UsuarioRepository;
import com.ecapi.service.TarjetaBusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarjetaBusServiceImpl implements TarjetaBusService {

    private final TarjetaBusRepository tarjetaBusRepo;
    private final UsuarioRepository usuarioRepo;
    private final MedioPagoRepository medioPagoRepo;
    private final TransaccionRepository transaccionRepo;

    @Override
    public TarjetaBusDTO consultarSaldo(String email) {
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("El email es requerido");
        }
        
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (usuario.getTarjeta() == null) {
            throw new RuntimeException("No tiene una tarjeta registrada");
        }
        
        TarjetaBus tarjeta = usuario.getTarjeta();
        
        if (tarjeta.getEstado() == 0) {
            throw new RuntimeException("La tarjeta está inactiva");
        }
        
        return convertToDTO(tarjeta);
    }

    @Override
    public List<MedioPagoDTO> obtenerMediosPago(String email) {
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("El email es requerido");
        }
        
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<MedioPago> mediosPago = medioPagoRepo.findByUsuarioAndEstado(usuario, 1);
        
        return mediosPago.stream()
                .map(this::convertMedioPagoToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RecargaResponseDTO recargarTarjeta(String email, RecargaRequest request) {
        
        RecargaResponseDTO response = new RecargaResponseDTO();
        response.setFechaTransaccion(LocalDateTime.now());
        response.setMontoRecargado(request.getMonto());
        
        try {
            // Validar email
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("El email es requerido");
            }
            
            // Buscar usuario
            Usuario usuario = usuarioRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Validar tarjeta (CUS20)
            if (usuario.getTarjeta() == null) {
                throw new RuntimeException("No tiene una tarjeta registrada");
            }
            
            TarjetaBus tarjeta = usuario.getTarjeta();
            
            if (tarjeta.getEstado() == 0) {
                throw new RuntimeException("La tarjeta está inactiva");
            }
            
            // Validar monto
            if (request.getMonto() <= 0) {
                throw new RuntimeException("El monto debe ser mayor a 0");
            }
            
            if (request.getMonto() > 500) {
                throw new RuntimeException("El monto máximo de recarga es S/ 500");
            }
            
            // Validar medio de pago
            if (request.getIdMedioPago() == null) {
                throw new RuntimeException("Debe seleccionar un medio de pago");
            }
            
            MedioPago medioPago = medioPagoRepo.findById(request.getIdMedioPago())
                    .orElseThrow(() -> new RuntimeException("Medio de pago no encontrado"));
            
            if (!medioPago.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("El medio de pago no pertenece al usuario");
            }
            
            if (medioPago.getEstado() == 0) {
                throw new RuntimeException("El medio de pago está inactivo");
            }
            
            // Validar saldo máximo
            double nuevoSaldo = tarjeta.getSaldo() + request.getMonto();
            
            if (nuevoSaldo > 1000) {
                throw new RuntimeException("El saldo máximo permitido es S/ 1000");
            }
            
            // Simular procesamiento de pago (en producción aquí iría la integración con pasarela)
            boolean pagoExitoso = procesarPago(medioPago, request.getMonto());
            
            if (!pagoExitoso) {
                // Registrar transacción rechazada
                registrarTransaccion(usuario, tarjeta, medioPago, request.getMonto(), "RECHAZADA");
                
                response.setExitosa(false);
                response.setMensaje("El pago fue rechazado por el banco. Intente con otro medio de pago.");
                return response;
            }
            
            // Actualizar saldo
            tarjeta.setSaldo(nuevoSaldo);
            tarjetaBusRepo.save(tarjeta);
            
            // Registrar transacción exitosa
            registrarTransaccion(usuario, tarjeta, medioPago, request.getMonto(), "EXITOSA");
            
            // Preparar respuesta exitosa
            response.setExitosa(true);
            response.setMensaje("Recarga realizada exitosamente");
            response.setTarjeta(convertToDTO(tarjeta));
            
            return response;
            
        } catch (RuntimeException e) {
            response.setExitosa(false);
            response.setMensaje(e.getMessage());
            return response;
        } catch (Exception e) {
            response.setExitosa(false);
            response.setMensaje("Error en el procesamiento. No se pudo completar la operación.");
            return response;
        }
    }
    
    private boolean procesarPago(MedioPago medioPago, double monto) {
        // Simulación de procesamiento de pago
        // En producción aquí iría la integración con la pasarela de pago
        // Por ahora siempre retorna true (éxito)
        return true;
    }
    
    private void registrarTransaccion(Usuario usuario, TarjetaBus tarjeta, MedioPago medioPago, 
                                     double monto, String estado) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo("RECARGA");
        transaccion.setMonto(monto);
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setEstado(estado);
        transaccion.setUsuario(usuario);
        transaccion.setTarjeta(tarjeta);
        transaccion.setMedioPago(medioPago);
        
        transaccionRepo.save(transaccion);
    }
    
    private TarjetaBusDTO convertToDTO(TarjetaBus tarjeta) {
        TarjetaBusDTO dto = new TarjetaBusDTO();
        dto.setId(tarjeta.getId());
        dto.setNumTarjeta(tarjeta.getNumTarjeta());
        dto.setSaldo(tarjeta.getSaldo());
        dto.setEstado(tarjeta.getEstado());
        dto.setFechaActualizacion(java.time.LocalDateTime.now());
        return dto;
    }
    
    private MedioPagoDTO convertMedioPagoToDTO(MedioPago medioPago) {
        MedioPagoDTO dto = new MedioPagoDTO();
        dto.setId(medioPago.getId());
        dto.setTipo(medioPago.getTipo());
        dto.setDescripcion(medioPago.getDescripcion());
        dto.setNumeroEnmascarado(medioPago.getNumeroEnmascarado());
        dto.setEstado(medioPago.getEstado());
        return dto;
    }
}
