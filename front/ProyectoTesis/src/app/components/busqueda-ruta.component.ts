import { Component, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { GeocodingService } from '../services/geocoding.service';
import { BusquedaRutaService } from '../services/busqueda-ruta.service';
import { MejorRutaResponseDTO } from '../models/mejor-ruta-response.dto';
import { MapLoaderService } from '../services/map-loader.service';

@Component({
  selector: 'app-busqueda-ruta',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './busqueda-ruta.component.html',
})
export class BusquedaRutaComponent implements AfterViewInit {

  origen = '';
  destino = '';
  resultado?: MejorRutaResponseDTO;

  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef<HTMLDivElement>;
  private map?: google.maps.Map;
  private markers: google.maps.Marker[] = [];
  private polyline?: google.maps.Polyline;

  private apiKey = 'AIzaSyBWdkrFQPP7uApLAj1NqEkaoEQT3Cp-4-8';

  constructor(
    private geocodingService: GeocodingService,
    private busquedaService: BusquedaRutaService,
    private mapLoader: MapLoaderService
  ) {}

  async ngAfterViewInit() {
    await this.mapLoader.load(this.apiKey);

    // Inicializar mapa con centro por defecto
    this.map = new google.maps.Map(this.mapContainer.nativeElement, {
      center: { lat: -12.0464, lng: -77.0428 }, // Lima por defecto
      zoom: 12,
    });
  }

  async buscar() {
    if (!this.map) return;

    try {
      // Geocodificar direcciones
      const origenCoords = await firstValueFrom(this.geocodingService.geocodeAddress(this.origen));
      const destinoCoords = await firstValueFrom(this.geocodingService.geocodeAddress(this.destino));

      // Consultar mejor ruta al backend
      this.busquedaService.mejorRuta(
        origenCoords.lat,
        origenCoords.lng,
        destinoCoords.lat,
        destinoCoords.lng
      ).subscribe(response => {
        this.resultado = response;
        console.log('Respuesta del backend:', response);

        // Limpiar marcadores y polilínea anteriores
        this.clearMap();

        // Dibujar nueva ruta
        this.drawRoute(origenCoords, destinoCoords, response);
      });

    } catch (error) {
      console.error(error);
      alert('Error buscando coordenadas o ruta.');
    }
  }

  private drawRoute(origen: { lat: number, lng: number }, destino: { lat: number, lng: number }, ruta?: MejorRutaResponseDTO) {
    if (!this.map) return;

    // Marcadores de origen y destino
    const markerOrigen = new google.maps.Marker({ position: origen, map: this.map, title: 'Origen' });
    const markerDestino = new google.maps.Marker({ position: destino, map: this.map, title: 'Destino' });
    this.markers.push(markerOrigen, markerDestino);

    // Ajustar vista al bounds
    const bounds = new google.maps.LatLngBounds();
    bounds.extend(origen);
    bounds.extend(destino);

    // Agregar puntos de la ruta si existen
    if (ruta?.paraderosRuta?.length) {
      const path = ruta.paraderosRuta.map(p => ({ lat: p.lat, lng: p.lng }));
      this.polyline = new google.maps.Polyline({
        path,
        map: this.map,
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 3
      });

      path.forEach(p => bounds.extend(p));
    }

    this.map.fitBounds(bounds);
  }

  private clearMap() {
    // Limpiar marcadores
    this.markers.forEach(m => m.setMap(null));
    this.markers = [];

    // Limpiar polilínea
    if (this.polyline) {
      this.polyline.setMap(null);
      this.polyline = undefined;
    }
  }
}
