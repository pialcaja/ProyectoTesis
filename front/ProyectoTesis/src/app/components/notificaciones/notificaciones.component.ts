import { CommonModule } from "@angular/common";
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from "@angular/core";
import { NavbarComponent } from "../navbar/navbar.component";
import { AuthService } from "../../services/auth.service";
import { Alerta, AlertaNotificacionService } from "../../services/alerta-notificacion.service";
import Swal from 'sweetalert2';

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
    this.alertaService.marcarComoEnviada(alertaId).subscribe({
      next: () => {
        this.alertas = this.alertas.filter(a => a.id !== alertaId);

        Swal.fire({
          icon: 'success',
          title: 'Alerta marcada',
          text: 'La notificación fue marcada como enviada',
          toast: true,
          position: 'top-end',
          timer: 3000,
          showConfirmButton: false
        });

        this.cdr.markForCheck();
      },
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo marcar la alerta',
        });
      }
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

        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudieron cargar las notificaciones'
        });

        this.cdr.markForCheck();
      }
    });
  }

  eliminar(alertaId: number) {
    Swal.fire({
      title: '¿Eliminar alerta?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33'
    }).then(result => {
      if (!result.isConfirmed) return;

      this.alertaService.eliminarAlerta(alertaId).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Eliminada',
            text: 'La alerta fue eliminada correctamente',
            toast: true,
            position: 'top-end',
            timer: 3000,
            showConfirmButton: false
          });

          this.cargarAlertas();
        },
        error: () => {
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudo eliminar la alerta'
          });
        }
      });
    });
  }
}