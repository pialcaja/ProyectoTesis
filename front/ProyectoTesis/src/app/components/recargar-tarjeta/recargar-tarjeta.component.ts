import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TarjetaBusService, TarjetaBus, MedioPago, RecargaRequest, RecargaResponse } from '../../services/tarjeta-bus.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-recargar-tarjeta',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './recargar-tarjeta.component.html',
  styleUrls: ['./recargar-tarjeta.component.css']
})
export class RecargarTarjetaComponent implements OnInit {


  tarjeta: TarjetaBus | null = null;
  numeroTarjetaIngresada: string | null = null;
  mediosPago: MedioPago[] = [];
  montos: number[] = [10, 20, 30, 50, 100];
  montoSeleccionado: number | null = null;
  montoPersonalizado: number | null = null;
  medioPagoSeleccionado: number | null = null;

  errorMsg: string = '';
  successMsg: string = '';
  loading: boolean = false;
  loadingData: boolean = true;
  sinTarjeta: boolean = false;
  sinMediosPago: boolean = false;

  mostrarConfirmacion: boolean = false;
  recargaExitosa: boolean = false;
  nuevoSaldo: number = 0;
  lastRecarga: RecargaResponse | null = null;
  mostrarComprobante: boolean = false;

  constructor(
    private tarjetaBusService: TarjetaBusService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
  }

  ngOnInit() {
    this.cargarDatos();

    setTimeout(() => {
      try {
        this.cdr.detectChanges();
      } catch (e) {
        console.warn('🔴 detectChanges() falló en ngOnInit', e);
      }
    });
  }

  cargarDatos() {
    this.loadingData = true;
    this.errorMsg = '';

    this.tarjetaBusService.consultarSaldo().subscribe({
      next: (data: TarjetaBus) => {
        this.tarjeta = data;

        this.cargarMediosPago();
      },
      error: (err: any) => {
        this.loadingData = false;

        if (err.error === 'No tiene una tarjeta registrada') {
          this.sinTarjeta = true;
          this.errorMsg = 'No tiene una tarjeta registrada. Por favor, registre una tarjeta antes de recargar.';
        } else {
          this.errorMsg = err.error || 'Error al cargar la información de la tarjeta.';
        }
      }
    });
  }

  cargarMediosPago() {

    this.tarjetaBusService.obtenerMediosPago().subscribe({
      next: (data: MedioPago[]) => {
        this.mediosPago = data;
        this.loadingData = false;

        try {
          this.cdr.detectChanges();
        } catch (e) {
          console.warn('🔴 detectChanges() falló en cargarMediosPago', e);
        }

        if (this.mediosPago.length === 0) {
          this.sinMediosPago = true;
          this.errorMsg = 'No tiene medios de pago registrados. Debe registrar uno desde el módulo "Medio de pago".';
        }
      },
      error: (err: any) => {
        this.loadingData = false;
        this.errorMsg = 'Error al cargar los medios de pago.';
      }
    });
  }

  seleccionarMonto(monto: number) {
    this.montoSeleccionado = monto;
    this.montoPersonalizado = null;
    this.errorMsg = '';
  }

  usarMontoPersonalizado() {
    if (this.montoPersonalizado && this.montoPersonalizado > 0) {
      this.montoSeleccionado = this.montoPersonalizado;
      this.errorMsg = '';
    }
  }

  seleccionarMedioPago(idMedioPago: number) {
    this.medioPagoSeleccionado = idMedioPago;
    this.errorMsg = '';
  }

  validarYMostrarConfirmacion() {
    this.errorMsg = '';

    if (!this.montoSeleccionado || this.montoSeleccionado <= 0) {
      this.errorMsg = 'Debe seleccionar o ingresar un monto válido';
      return;
    }

    if (this.montoSeleccionado > 500) {
      this.errorMsg = 'El monto máximo de recarga es S/ 500';
      return;
    }

    if (!this.medioPagoSeleccionado) {
      this.errorMsg = 'Debe seleccionar un medio de pago';
      return;
    }

    const nuevoSaldo = (this.tarjeta?.saldo || 0) + this.montoSeleccionado;

    if (nuevoSaldo > 1000) {
      this.errorMsg = 'El saldo máximo permitido es S/ 1000.';
      return;
    }

    this.mostrarConfirmacion = true;
  }

  cancelarRecarga() {
    this.mostrarConfirmacion = false;
    this.errorMsg = '';
  }

  confirmarRecarga() {
    if (!this.montoSeleccionado || !this.medioPagoSeleccionado) {
      return;
    }

    this.loading = true;
    this.errorMsg = '';
    this.successMsg = '';

    const request: RecargaRequest = {
      monto: this.montoSeleccionado,
      idMedioPago: this.medioPagoSeleccionado
    };

    this.tarjetaBusService.recargarTarjeta(request).subscribe({
      next: (response: RecargaResponse) => {

        this.loading = false;
        this.mostrarConfirmacion = false;

        if (response.exitosa) {
          this.recargaExitosa = true;
          this.lastRecarga = response;
          this.successMsg = response.mensaje;
          this.nuevoSaldo = response.tarjeta?.saldo || 0;
          this.tarjeta = response.tarjeta || this.tarjeta;

          try {
            this.cdr.detectChanges();
          } catch (e) {
            console.warn("🔴 detectChanges() falló en confirmarRecarga (SUCCESS)", e);
          }

        } else {
          this.errorMsg = response.mensaje;
        }
      },
      error: (err: any) => {
        this.loading = false;
        this.mostrarConfirmacion = false;
        this.errorMsg = err.error || 'Error al procesar la recarga.';
      }
    });
  }

  abrirComprobante() {
    this.mostrarComprobante = true;
  }

  cerrarComprobante() {
    this.mostrarComprobante = false;
  }

  nuevaRecarga() {
    this.recargaExitosa = false;
    this.montoSeleccionado = null;
    this.montoPersonalizado = null;
    this.medioPagoSeleccionado = null;
    this.errorMsg = '';
    this.successMsg = '';
  }

  irAConsultar() {
    this.router.navigate(['/consultar-saldo']);
  }

  getMedioPagoDescripcion(id: number): string {
    const medio = this.mediosPago.find(m => m.id === id);
    const desc = medio ? `${medio.descripcion} ${medio.numeroEnmascarado}` : '';

    return desc;
  }

  imprimirComprobante() {
    window.print();
  }

}
