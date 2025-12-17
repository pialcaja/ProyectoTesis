import { CommonModule } from "@angular/common";
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from "@angular/core";
import { NavbarComponent } from "../navbar/navbar.component";
import { AuthService } from "../../services/auth.service";
import { Alerta, AlertaNotificacionService } from "../../services/alerta-notificacion.service";

@Component({
  standalone: true,
  selector: 'app-notificaciones-view',
  imports: [CommonModule, NavbarComponent],
  templateUrl: './notificaciones.component.html',
  styleUrls: ['./notificaciones.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NotificacionesComponent implements OnInit {

  isLoggedIn = false;
  username = '';
  cargando = true;

  private cdr = inject(ChangeDetectorRef);

  alertas: Alerta[] = [];

  constructor(public authService: AuthService, private alertaService: AlertaNotificacionService) { }

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.username = this.authService.getUsername();
    });

    this.cargarAlertas();
  }

  marcar(alertaId: number) {
    this.alertaService.marcarComoEnviada(alertaId)
      .subscribe(() => {
        this.alertas = this.alertas.filter(a => a.id !== alertaId);
      });
  }

  cargarAlertas() {
    const userId = this.authService.getUserId();

    this.alertaService.listarAlertasPorUsuario(userId).subscribe({
      next: data => {
        this.alertas = data;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  eliminar(alertaId: number) {
    if (!confirm('¿Eliminar esta alerta?')) return;

    this.alertaService.eliminarAlerta(alertaId).subscribe({
      next: () => this.cargarAlertas(),
      error: err => console.error(err)
    });
  }

  logout() {
    this.authService.logout();
  }
}