import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeocodingService {

  private apiKey = 'AIzaSyBWdkrFQPP7uApLAj1NqEkaoEQT3Cp-4-8';

  constructor(private http: HttpClient) {}

  geocodeAddress(address: string): Observable<{ lat: number, lng: number }> {
    const url =
      `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(address)}&key=${this.apiKey}`;

    return this.http.get<any>(url).pipe(
      map(response => {
        if (response.status === 'OK') {
          const location = response.results[0].geometry.location;
          return { lat: location.lat, lng: location.lng };
        }
        throw new Error('No se encontraron coordenadas');
      })
    );
  }
}
