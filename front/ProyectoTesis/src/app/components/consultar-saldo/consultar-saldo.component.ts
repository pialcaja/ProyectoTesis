import { Component, OnInit, OnDestroy, AfterViewInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationStart } from '@angular/router';
import { TarjetaBusService, TarjetaBus } from '../../services/tarjeta-bus.service';
import { Subscription } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-consultar-saldo',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './consultar-saldo.component.html',
  styleUrls: ['./consultar-saldo.component.css']
})
export class ConsultarSaldoComponent implements OnInit, AfterViewInit, OnDestroy {

  tarjeta: TarjetaBus | null = null;
  errorMsg: string = '';
  loading: boolean = false;
  sinTarjeta: boolean = false;

  private subs = new Subscription();
  private routerEventsSub?: Subscription;
  private cdr = inject(ChangeDetectorRef);

  constructor(
    private tarjetaBusService: TarjetaBusService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.consultarSaldo();
  }

  ngAfterViewInit() {
  }

  consultarSaldo() {
    this.loading = true;
    this.errorMsg = '';
    this.tarjeta = null;
    this.sinTarjeta = false;

    const s = this.tarjetaBusService.consultarSaldo().subscribe({
      next: (data: TarjetaBus) => {
        this.tarjeta = data;
        this.loading = false;
        this.errorMsg = '';

        try {
          this.cdr.detectChanges();
        } catch (e) {
          console.warn('detectChanges() falló', e);
        }
      },
      error: (err: any) => {
        this.loading = false;

        if (err?.error === 'No tiene una tarjeta registrada') {
          this.sinTarjeta = true;
          this.errorMsg = 'No tiene una tarjeta registrada. Por favor, registre una tarjeta para consultar el saldo.';
        } else {
          this.errorMsg = err?.error || 'No es posible mostrar los datos en este momento. Intente nuevamente más tarde.';
        }


      }
    });

    this.subs.add(s);
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

  ngOnDestroy() {
    this.subs.unsubscribe();
    this.routerEventsSub?.unsubscribe();
  }
}
