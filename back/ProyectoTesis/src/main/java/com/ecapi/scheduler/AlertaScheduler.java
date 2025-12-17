package com.ecapi.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecapi.service.AlertaNotificacionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlertaScheduler {

    private final AlertaNotificacionService alertaService;

    @Scheduled(fixedRate = 60000)
    public void revisarAlertas() {
        System.out.println("⏰ Scheduler ejecutado: " + LocalDateTime.now());
        alertaService.procesarAlertasPendientes();
    }
}

