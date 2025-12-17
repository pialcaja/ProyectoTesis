import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Alerta, AlertaNotificacionService } from '../../services/alerta-notificacion.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent implements OnInit {

  isLoggedIn = false;
  username = '';
  menuOpen = false;

  dropdownOpen: string | null = null;

  alertas: Alerta[] = [];
  contador = 0;

  private cdr = inject(ChangeDetectorRef);

  constructor(public authService: AuthService, private alertaService: AlertaNotificacionService) { }

  notificadas = new Set<number>(); // IDs de alertas ya notificadas
  private pollingSub?: Subscription;

  ngOnInit(): void {
    // Actualiza estado de login y username
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.username = this.authService.getUsername();
    });

    const userId = this.authService.getUserId();

    // Evita múltiples suscripciones
    if (!this.pollingSub) {
      this.pollingSub = this.alertaService.pollingAlertas(userId, 60)
        .subscribe(alertas => {
          console.log('===== Polling ejecutado =====');
          console.log('Alertas recibidas del backend:', alertas);

          const ahora = new Date();
          const pendientes = alertas.filter(a => a.estado === 'PENDIENTE');
          console.log('Alertas pendientes del backend:', pendientes);

          pendientes.forEach(a => {
            const fechaEnvio = new Date(a.fechaEnvio);
            console.log(a.id, a.estado, a.fechaEnvio);

            if (!this.notificadas.has(a.id) && fechaEnvio <= ahora) {
              // Marcar como notificadas para que no se repitan
              this.notificadas.add(a.id);

              // Notificación tipo alert
              alert(`🚌 Bus por llegar\nRuta: ${a.rutaNombre}\nParadero: ${a.paraderoNombre}`);

              // Notificación de navegador opcional
              this.mostrarNotificacion(a);
            }
          });

          // Contador solo de alertas pendientes cuya fecha ya pasó
          this.contador = pendientes.filter(a => new Date(a.fechaEnvio) <= ahora).length;

          // Actualiza la lista de alertas
          this.alertas = alertas;

          console.log('Contador actualizado:', this.contador);
          console.log('=============================');

          this.cdr.markForCheck();
        });
    }
  }

  ngOnDestroy(): void {
    this.pollingSub?.unsubscribe();
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

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  toggleDropdown(name: string) {
    this.dropdownOpen = this.dropdownOpen === name ? null : name;
  }

  logout() {
    this.authService.logout();
  }
}
