import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, interval, switchMap } from 'rxjs';

export interface Alerta {
  id: number;
  rutaNombre: string;
  paraderoNombre: string;
  sentido: string;
  minutosAntes: number;
  estado: string;
  fechaEnvio: string;
}

export interface CrearAlertaRequest {
  usuarioId: number;
  rutaId: number;
  paraderoId: number;
  sentido: string;
  minutosAntes: number;
  horaLlegadaAproximada: string;
}

@Injectable({
  providedIn: 'root'
})
export class AlertaNotificacionService {

  private API = 'http://localhost:8080/api/alertas';

  constructor(private http: HttpClient) {}

  getAlertasPendientes(usuarioId: number): Observable<Alerta[]> {
    return this.http.get<Alerta[]>(`${this.API}/usuario/${usuarioId}/pendientes`);
  }

  marcarComoEnviada(alertaId: number): Observable<void> {
    return this.http.patch<void>(
      `${this.API}/${alertaId}/estado`,
      null,
      { params: { estado: 'ENVIADA' } }
    );
  }

  pollingAlertas(usuarioId: number, segundos = 60) {
    return interval(segundos * 1000).pipe(
      switchMap(() => this.getAlertasPendientes(usuarioId))
    );
  }

  crearAlerta(request: CrearAlertaRequest): Observable<Alerta> {
    return this.http.post<Alerta>(this.API, request);
  }

  listarAlertasPorUsuario(usuarioId: number): Observable<Alerta[]> {
    return this.http.get<Alerta[]>(`${this.API}/usuario/${usuarioId}`);
  }

  eliminarAlerta(alertaId: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${alertaId}`);
  }
}
