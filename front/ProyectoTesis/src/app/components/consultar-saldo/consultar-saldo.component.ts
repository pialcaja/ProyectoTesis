import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TarjetaBusService, TarjetaBus } from '../../services/tarjeta-bus.service';

@Component({
  selector: 'app-consultar-saldo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './consultar-saldo.component.html',
  styleUrl: './consultar-saldo.component.css'
})
export class ConsultarSaldoComponent {

  tarjeta: TarjetaBus | null = null;
  errorMsg: string = '';
  loading: boolean = false;
  sinTarjeta: boolean = false;

  constructor(
    private tarjetaBusService: TarjetaBusService,
    private router: Router
  ) {
    this.consultarSaldo();
  }

  consultarSaldo() {
    this.loading = true;
    this.errorMsg = '';
    this.tarjeta = null;
    this.sinTarjeta = false;

    this.tarjetaBusService.consultarSaldo().subscribe({
      next: (data: TarjetaBus) => {
        this.tarjeta = data;
        this.loading = false;
      },
      error: (err: any) => {
        this.loading = false;
        if (err.error === 'No tiene una tarjeta registrada') {
          this.sinTarjeta = true;
          this.errorMsg = 'No tiene una tarjeta registrada. Por favor, registre una tarjeta para consultar el saldo.';
        } else {
          this.errorMsg = err.error || 'No es posible mostrar los datos en este momento. Intente nuevamente más tarde.';
        }
      }
    });
  }

  irARecargar() {
    this.router.navigate(['/recargar-tarjeta']);
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleString('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
