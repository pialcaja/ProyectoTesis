import { Component, ElementRef, ViewChild, AfterViewInit, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BusquedaRutaService } from '../../services/busqueda-ruta.service';
import { MapLoaderService } from '../../services/map-loader.service';
import { MejorRutaResponseDTO, ParaderoDTO } from '../../models/mejor-ruta-response.dto';
import { GeocodingService } from '../../services/geocoding.service';
import { AuthService } from '../../services/auth.service';
import { NavbarComponent } from '../navbar/navbar.component';
import { AlertaNotificacionService, CrearAlertaRequest } from '../../services/alerta-notificacion.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-busqueda-ruta',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './busqueda-ruta.component.html',
  styleUrl: './busqueda-ruta.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BusquedaRutaComponent implements AfterViewInit {

  resultado?: MejorRutaResponseDTO;
  paraderoOrigen?: ParaderoDTO;
  paraderoDestino?: ParaderoDTO;
  paraderoSeleccionado?: ParaderoDTO;

  isLoggedIn = false;
  username = '';

  origenTieneTexto = false;
  destinoTieneTexto = false;

  mostrarModalAlerta = false;
  modalAbierto = false;

  minutosAntes = 5;

  usandoUbicacionActual = false;
  ubicacionActualCoords?: { lat: number; lng: number };

  @ViewChild('mapContainer', { static: false })
  mapContainer!: ElementRef<HTMLDivElement>;

  @ViewChild('origenInput')
  origenInput!: ElementRef<HTMLInputElement>;

  @ViewChild('destinoInput')
  destinoInput!: ElementRef<HTMLInputElement>;

  private map?: google.maps.Map;

  private autocompleteOrigen?: google.maps.places.Autocomplete;
  private autocompleteDestino?: google.maps.places.Autocomplete;

  private directionsService?: google.maps.DirectionsService;
  private directionsRenderer?: google.maps.DirectionsRenderer;

  private markers: google.maps.marker.AdvancedMarkerElement[] = [];
  private markerUbicacionActual?: google.maps.marker.AdvancedMarkerElement;

  private polyline?: google.maps.Polyline;

  private apiKey = 'AIzaSyBWdkrFQPP7uApLAj1NqEkaoEQT3Cp-4-8';

  constructor(
    private busquedaService: BusquedaRutaService,
    private authService: AuthService,
    private mapLoader: MapLoaderService,
    private geocodingService: GeocodingService,
    private cdr: ChangeDetectorRef,
    private alertaService: AlertaNotificacionService
  ) { }

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.username = this.authService.getUsername();
    });
  }

  async ngAfterViewInit() {
    await this.mapLoader.load(this.apiKey, ['places', 'marker']);

    this.map = new google.maps.Map(this.mapContainer.nativeElement, {
      center: { lat: -12.0464, lng: -77.0428 },
      zoom: 12,
      mapId: '8c9e07024846a3a91b05df16'
    });

    this.autocompleteOrigen = new google.maps.places.Autocomplete(this.origenInput.nativeElement, {
      fields: ['geometry', 'name'],
      strictBounds: false
    });

    this.autocompleteDestino = new google.maps.places.Autocomplete(this.destinoInput.nativeElement, {
      fields: ['geometry', 'name'],
      strictBounds: false
    });

    this.directionsService = new google.maps.DirectionsService();

    this.directionsRenderer = new google.maps.DirectionsRenderer({
      map: this.map,
      suppressMarkers: true,
      polylineOptions: {
        strokeColor: '#FF0000',
        strokeWeight: 4
      }
    });
  }

  async buscar() {
    if (!this.map || !this.autocompleteDestino) return;

    let origenCoords: { lat: number; lng: number };

    if (this.usandoUbicacionActual && this.ubicacionActualCoords) {
      origenCoords = this.ubicacionActualCoords;
    } else {
      const origenPlace = this.autocompleteOrigen?.getPlace();

      if (!origenPlace?.geometry?.location) {
        Swal.fire({
          icon: 'warning',
          title: 'Origen inválido',
          text: 'Selecciona un origen válido'
        });
        return;
      }

      origenCoords = {
        lat: origenPlace.geometry.location.lat(),
        lng: origenPlace.geometry.location.lng()
      };
    }

    const destinoPlace = this.autocompleteDestino.getPlace();

    if (!destinoPlace?.geometry?.location) {
      Swal.fire({
        icon: 'warning',
        title: 'Destino inválido',
        text: 'Selecciona un destino válido'
      });
      return;
    }

    const destinoCoords = {
      lat: destinoPlace.geometry.location.lat(),
      lng: destinoPlace.geometry.location.lng()
    };

    this.busquedaService.mejorRuta(
      origenCoords.lat,
      origenCoords.lng,
      destinoCoords.lat,
      destinoCoords.lng
    ).subscribe({
      next: (data) => {
        if (!data.paraderosRuta || !data.paraderosRuta.length) {
          this.resultado = undefined;
          this.clearMap();
          Swal.fire({
            icon: 'error',
            title: 'Sin resultados',
            text: 'No se encontró una ruta para los puntos seleccionados'
          });
          return;
        }

        this.resultado = data;

        this.paraderoOrigen = data.paraderosRuta[0];
        this.paraderoDestino = data.paraderosRuta[data.paraderosRuta.length - 1];

        this.drawRoute(data);
        this.cdr.markForCheck();
      },
      error: () => {
        this.resultado = undefined;
        this.modalAbierto = false;
        this.clearMap();

        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Error al buscar la ruta'
        });
      }
    });

  }

  private drawRoute(ruta?: MejorRutaResponseDTO) {
    if (!this.map || !this.directionsService) return;
    if (!ruta?.paraderosRuta?.length) return;

    this.clearMap();

    const paraderos = ruta.paraderosRuta;
    const MAX_WAYPOINTS = 25;

    const paraderoInicio = { lat: paraderos[0].lat, lng: paraderos[0].lng };
    const paraderoFin = { lat: paraderos[paraderos.length - 1].lat, lng: paraderos[paraderos.length - 1].lng };

    this.markers.push(
      new google.maps.marker.AdvancedMarkerElement({ position: paraderoInicio, map: this.map, title: 'Paradero Inicial' }),
      new google.maps.marker.AdvancedMarkerElement({ position: paraderoFin, map: this.map, title: 'Paradero Final' })
    );

    const slices: ParaderoDTO[][] = [];
    let start = 0;
    while (start < paraderos.length - 1) {
      const end = Math.min(start + MAX_WAYPOINTS + 1, paraderos.length);
      slices.push(paraderos.slice(start, end));
      start = end - 1;
    }

    slices.forEach(segment => {
      if (segment.length < 2) return;

      const origin = { lat: segment[0].lat, lng: segment[0].lng };
      const destination = { lat: segment[segment.length - 1].lat, lng: segment[segment.length - 1].lng };

      const waypoints = segment.slice(1, -1).map(p => ({
        location: { lat: p.lat, lng: p.lng },
        stopover: true
      }));

      const renderer = new google.maps.DirectionsRenderer({
        map: this.map,
        suppressMarkers: true,
        polylineOptions: { strokeColor: '#FF0000', strokeWeight: 4 }
      });

      this.directionsService!.route(
        {
          origin,
          destination,
          waypoints,
          travelMode: google.maps.TravelMode.DRIVING
        },
        (result, status) => {
          if (status === google.maps.DirectionsStatus.OK && result) {
            renderer.setDirections(result);
            this.markers.push(renderer as any);
          }
        }
      );
    });
  }

  private clearMap() {
    this.markers.forEach(m => m.map = null);
    this.markers = [];
    if (this.directionsRenderer) {
      this.directionsRenderer.setDirections({ routes: [] } as any);
    }
  }

  usarUbicacionActual() {
    if (!navigator.geolocation) {
      Swal.fire({
        icon: 'error',
        title: 'Geolocalización no disponible'
      });
      return;
    }

    navigator.geolocation.getCurrentPosition(position => {
      if (!this.map) return;

      const pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };

      const marker = new google.maps.marker.AdvancedMarkerElement({
        position: pos,
        map: this.map,
        title: 'Tu ubicación actual'
      });

      this.map.setCenter(pos);
      this.map.setZoom(15);

      this.markers.push(marker);
      this.markerUbicacionActual = marker;
      this.ubicacionActualCoords = pos;
      this.usandoUbicacionActual = true;
      this.origenTieneTexto = false;

      this.cdr.markForCheck();
    });
  }

  dejarDeUsarUbicacion() {
    this.usandoUbicacionActual = false;

    if (this.markerUbicacionActual) {
      this.markerUbicacionActual.map = null;
      this.markerUbicacionActual = undefined;
    }

    this.ubicacionActualCoords = undefined;

    this.resultado = undefined;
    this.clearMap();
  }


  onOrigenInputChange() {
    const value = this.origenInput.nativeElement.value;
    this.origenTieneTexto = value.trim().length > 0;
  }

  onDestinoInputChange() {
    const value = this.destinoInput.nativeElement.value;
    this.destinoTieneTexto = value.trim().length > 0;
  }

  logout() {
    this.authService.logout();
  }

  abrirModalParaderos() {
    if (!this.resultado || !this.resultado.paraderosRuta?.length) return;
    this.modalAbierto = true;
  }

  cerrarModalParaderos() {
    this.modalAbierto = false;
  }

  abrirModalAlerta(paradero: ParaderoDTO) {
    this.paraderoSeleccionado = paradero;
    this.mostrarModalAlerta = true;

    if ('Notification' in window && Notification.permission === 'default') {
      Notification.requestPermission();
    }
  }

  cerrarModal() {
    this.mostrarModalAlerta = false;
  }

  crearAlerta() {
    if (!this.resultado || !this.resultado.rutaId || !this.resultado.sentido || !this.paraderoSeleccionado) {
      Swal.fire({
        icon: 'warning',
        title: 'Información incompleta',
        text: 'No hay datos suficientes para crear la alerta'
      });
      return;
    }

    const horaLlegada = this.paraderoSeleccionado.horaLlegadaAproximada;
    if (!horaLlegada) {
      Swal.fire({
        icon: 'error',
        title: 'Hora no disponible',
        text: 'No se pudo obtener la hora de llegada del paradero'
      });
      return;
    }

    const request: CrearAlertaRequest = {
      usuarioId: this.authService.getUserId(),
      rutaId: this.resultado.rutaId,
      paraderoId: this.paraderoSeleccionado.id,
      sentido: this.resultado.sentido,
      minutosAntes: this.minutosAntes,
      horaLlegadaAproximada: horaLlegada
    };

    this.alertaService.crearAlerta(request).subscribe({
      next: () => {
        this.mostrarModalAlerta = false;
        this.modalAbierto = false;
        this.cdr.markForCheck();

        setTimeout(() => {
          Swal.fire({
            icon: 'success',
            title: 'Alerta creada',
            text: 'Te notificaremos antes de la llegada del bus',
            confirmButtonText: 'Aceptar'
          });
        }, 100);
      },
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo crear la alerta'
        });
      }
    });
  }
}
