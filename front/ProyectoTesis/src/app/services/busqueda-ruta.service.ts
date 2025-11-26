import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MejorRutaResponseDTO } from '../models/mejor-ruta-response.dto';

@Injectable({
  providedIn: 'root'
})
export class BusquedaRutaService {

  private apiUrl = 'http://localhost:8080/api/busqueda/mejor-ruta';

  constructor(private http: HttpClient) {}

  obtenerMejorRuta(
    latOrigen: number,
    lngOrigen: number,
    latDestino: number,
    lngDestino: number
  ): Observable<MejorRutaResponseDTO> {

    let params = new HttpParams()
      .set('latOrigen', latOrigen)
      .set('lngOrigen', lngOrigen)
      .set('latDestino', latDestino)
      .set('lngDestino', lngDestino);

    return this.http.get<MejorRutaResponseDTO>(this.apiUrl, { params });
  }
}
