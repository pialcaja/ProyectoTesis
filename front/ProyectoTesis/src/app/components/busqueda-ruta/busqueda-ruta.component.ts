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


  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef<HTMLDivElement>;
  @ViewChild('origenInput') origenInput!: ElementRef<HTMLInputElement>;
  @ViewChild('destinoInput') destinoInput!: ElementRef<HTMLInputElement>;

  private map?: google.maps.Map;
  private markers: google.maps.Marker[] = [];
  private polyline?: google.maps.Polyline;

  private autocompleteOrigen?: google.maps.places.Autocomplete;
  private autocompleteDestino?: google.maps.places.Autocomplete;

  isLoggedIn = false;
  username = '';

  origenTieneTexto = false;
  destinoTieneTexto = false;

  mostrarModalAlerta = false;
  paraderoSeleccionado?: ParaderoDTO;
  minutosAntes = 5;

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
    await this.mapLoader.load(this.apiKey, ['places']);

    this.map = new google.maps.Map(this.mapContainer.nativeElement, {
      center: { lat: -12.0464, lng: -77.0428 },
      zoom: 12,
    });

    this.autocompleteOrigen = new google.maps.places.Autocomplete(this.origenInput.nativeElement, {
      fields: ['geometry', 'name'],
      strictBounds: false
    });

    this.autocompleteDestino = new google.maps.places.Autocomplete(this.destinoInput.nativeElement, {
      fields: ['geometry', 'name'],
      strictBounds: false
    });
  }

  async buscar() {
    if (!this.map || !this.autocompleteOrigen || !this.autocompleteDestino) return;

    const origenPlace = this.autocompleteOrigen.getPlace();
    const destinoPlace = this.autocompleteDestino.getPlace();

    if (!origenPlace?.geometry?.location || !destinoPlace?.geometry?.location) {
      alert('Selecciona direcciones válidas de la lista desplegable.');
      return;
    }

    const origenCoords = {
      lat: origenPlace.geometry.location.lat(),
      lng: origenPlace.geometry.location.lng()
    };

    const destinoCoords = {
      lat: destinoPlace.geometry.location.lat(),
      lng: destinoPlace.geometry.location.lng()
    };

    this.busquedaService.mejorRuta(
      origenCoords.lat,
      origenCoords.lng,
      destinoCoords.lat,
      destinoCoords.lng
    ).subscribe(response => {
      this.resultado = response;

      if (response.paraderosRuta?.length) {
        this.paraderoOrigen = response.paraderosRuta[0];
        this.paraderoDestino = response.paraderosRuta[response.paraderosRuta.length - 1];
      }

      this.cdr.markForCheck();
      this.clearMap();
      this.drawRoute(origenCoords, destinoCoords, response);
    });
  }

  private drawRoute(origen: { lat: number, lng: number }, destino: { lat: number, lng: number }, ruta?: MejorRutaResponseDTO) {
    if (!this.map) return;

    this.clearMap();

    this.markers.push(
      new google.maps.Marker({ position: origen, map: this.map, title: 'Origen' }),
      new google.maps.Marker({ position: destino, map: this.map, title: 'Destino' })
    );

    const bounds = new google.maps.LatLngBounds();
    bounds.extend(origen);
    bounds.extend(destino);

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
    this.markers.forEach(m => m.setMap(null));
    this.markers = [];
    if (this.polyline) { this.polyline.setMap(null); this.polyline = undefined; }
  }

  usandoUbicacionActual = false;
  markerUbicacionActual?: google.maps.Marker;

  usarUbicacionActual() {
    if (!navigator.geolocation) {
      alert('La geolocalización no es soportada por este navegador.');
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;

        if (!this.map) return;

        const geocoder = new google.maps.Geocoder();

        geocoder.geocode({ location: { lat, lng } }, (results, status) => {
          if (status === 'OK' && results && results.length > 0) {
            const direccion = results[0].formatted_address;

            this.origenInput.nativeElement.value = direccion;
            this.origenInput.nativeElement.disabled = true;

            const pos = { lat, lng };
            const marker = new google.maps.Marker({
              position: pos,
              map: this.map,
              title: "Tu Ubicación Actual"
            });

            this.map?.setCenter(pos);
            this.map?.setZoom(15);

            this.markers.push(marker);
            this.markerUbicacionActual = marker;
            this.usandoUbicacionActual = true;

            google.maps.event.clearInstanceListeners(this.origenInput.nativeElement);

            this.origenTieneTexto = true;

            this.cdr.markForCheck();
          } else {
            alert('No se encontró la dirección de tu ubicación.');
          }
        });
      },
      (error) => {
        console.error(error);
        alert("No se pudo obtener tu ubicación. Asegúrate de permitir el acceso.");
      }
    );
  }

  dejarDeUsarUbicacion() {
    this.usandoUbicacionActual = false;

    this.origenInput.nativeElement.disabled = false;
    this.origenInput.nativeElement.value = '';

    if (this.markerUbicacionActual) {
      this.markerUbicacionActual.setMap(null);
      this.markerUbicacionActual = undefined;
    }

    if (this.origenInput) {
      this.autocompleteOrigen = new google.maps.places.Autocomplete(
        this.origenInput.nativeElement,
        { fields: ['geometry', 'name'], strictBounds: false }
      );
    }

    this.origenTieneTexto = false;
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

  modalAbierto = false;

  abrirModalParaderos() {
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
    if (
      !this.resultado ||
      !this.resultado.rutaId ||
      !this.resultado.sentido ||
      !this.paraderoSeleccionado
    ) {
      alert('No hay información suficiente para crear la alerta');
      return;
    }

    // Tomar la hora aproximada del paradero seleccionado
    const horaLlegada = this.paraderoSeleccionado.horaLlegadaAproximada;
    if (!horaLlegada) {
      alert('No se pudo obtener la hora de llegada del paradero seleccionado');
      return;
    }

    const request: CrearAlertaRequest = {
      usuarioId: this.authService.getUserId(),
      rutaId: this.resultado.rutaId,
      paraderoId: this.paraderoSeleccionado.id,
      sentido: this.resultado.sentido,
      minutosAntes: this.minutosAntes,
      horaLlegadaAproximada: horaLlegada // <--- Enviamos la hora
    };

    this.alertaService.crearAlerta(request).subscribe({
      next: () => {
        alert('🔔 Alerta creada correctamente');
        this.cerrarModal();
      },
      error: (err) => {
        console.error(err);
        alert('❌ Error al crear la alerta');
      }
    });
  }

}
