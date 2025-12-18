import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Alerta, AlertaNotificacionService } from '../../services/alerta-notificacion.service';
import { EMPTY, Subject, Subscription, switchMap, takeUntil, tap } from 'rxjs';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent implements OnInit, OnDestroy {

  alertas: Alerta[] = [];
  contador = 0;
  dropdownOpen: string | null = null;
  isLoggedIn = false;
  menuOpen = false;
  notificadas = new Set<number>();
  username = '';

  private destroy$ = new Subject<void>();
  private cdr = inject(ChangeDetectorRef);

  constructor(
    public authService: AuthService,
    private alertaService: AlertaNotificacionService
  ) { }

  ngOnInit(): void {

    this.authService.isLoggedIn$
      .pipe(
        takeUntil(this.destroy$),
        tap(status => {
          this.isLoggedIn = status;
          this.username = this.authService.getUsername();
        }),
        switchMap(isLoggedIn => {

          if (!isLoggedIn) {
            this.alertas = [];
            this.contador = 0;
            this.notificadas.clear();
            return EMPTY;
          }

          const userId = this.authService.getUserId();
          return this.alertaService.pollingAlertas(userId, 30);
        })
      )
      .subscribe(alertas => {

        const ahora = new Date();
        const pendientes = alertas.filter(a => a.estado === 'PENDIENTE');

        pendientes.forEach(a => {
          const fechaEnvio = new Date(a.fechaEnvio);

          if (fechaEnvio <= ahora && !this.notificadas.has(a.id)) {

            Swal.fire({
              icon: 'info',
              title: '🚌 Bus por llegar',
              html: `
                <div style="text-align:left">
                  <p><strong>Ruta:</strong> ${a.rutaNombre}</p>
                  <p><strong>Paradero:</strong> ${a.paraderoNombre}</p>
                </div>
              `,
              toast: true,
              position: 'top-end',
              showConfirmButton: false,
              timer: 6000,
              timerProgressBar: true
            });

            this.mostrarNotificacion(a);
            this.notificadas.add(a.id);
          }
        });

        this.contador = pendientes.filter(
          a => new Date(a.fechaEnvio) <= ahora
        ).length;

        this.alertas = alertas;
        this.cdr.markForCheck();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  mostrarNotificacion(alerta: Alerta) {
    if (Notification.permission === 'granted') {
      new Notification('🚌 Bus por llegar', {
        body: `Ruta ${alerta.rutaNombre}
Paradero: ${alerta.paraderoNombre}`,
        icon: 'assets/bus.png'
      });
    }
  }

  verAlerta(alerta: Alerta) {
    this.alertaService.marcarComoEnviada(alerta.id).subscribe(() => {
      this.alertas = this.alertas.filter(a => a.id !== alerta.id);
      this.contador = this.alertas.length;
    });

    this.dropdownOpen = null;
  }

  toggleDropdown(name: string) {
    this.dropdownOpen = this.dropdownOpen === name ? null : name;
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  logout() {
    this.authService.logout();
  }
}
