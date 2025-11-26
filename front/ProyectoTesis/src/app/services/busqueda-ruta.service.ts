// busqueda-ruta.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MejorRutaResponseDTO } from '../models/mejor-ruta-response.dto';

@Injectable({
  providedIn: 'root'
})
export class BusquedaRutaService {

  private apiUrl = 'http://localhost:8080/api/busqueda';

  constructor(private http: HttpClient) {}

  mejorRuta(latOrigen: number, lngOrigen: number, latDestino: number, lngDestino: number):
    Observable<MejorRutaResponseDTO> {

    return this.http.get<MejorRutaResponseDTO>(
      `${this.apiUrl}/mejor-ruta`, {
        params: {
          latOrigen,
          lngOrigen,
          latDestino,
          lngDestino
        }
      }
    );
  }
}

