import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroments/enviroment';

export interface TarjetaBus {
  id: number;
  numTarjeta: string;
  saldo: number;
  estado: number;
  fechaActualizacion: string;
}

export interface MedioPago {
  id: number;
  tipo: string;
  descripcion: string;
  numeroEnmascarado: string;
  estado: number;
}

export interface RecargaRequest {
  monto: number;
  idMedioPago: number;
}

export interface RecargaResponse {
  exitosa: boolean;
  mensaje: string;
  tarjeta?: TarjetaBus;
  montoRecargado: number;
  fechaTransaccion: string;
}

@Injectable({
  providedIn: 'root'
})
export class TarjetaBusService {

  private API = `${environment.apiBaseUrl}/tarjeta`;

  constructor(private http: HttpClient) { }

  consultarSaldo(): Observable<TarjetaBus> {
    return this.http.get<TarjetaBus>(`${this.API}/saldo`);
  }

  obtenerMediosPago(): Observable<MedioPago[]> {
    return this.http.get<MedioPago[]>(`${this.API}/medios-pago`);
  }

  recargarTarjeta(request: RecargaRequest): Observable<RecargaResponse> {
    return this.http.post<RecargaResponse>(`${this.API}/recargar`, request);
  }
}
