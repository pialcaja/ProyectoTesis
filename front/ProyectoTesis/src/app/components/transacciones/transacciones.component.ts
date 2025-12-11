import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { TarjetaBusService, Transaccion } from '../../services/tarjeta-bus.service';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  standalone: true,
  selector: 'app-transacciones-view',
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './transacciones.component.html',
  styleUrls: ['./transacciones.component.css']
})
export class TransaccionesComponent implements OnInit {

  isLoggedIn = false;
  username = '';

  // Filtros
  estado: string = '';
  fechaDesde: string = '';
  fechaHasta: string = '';

  // Datos
  transacciones: Transaccion[] = [];

  // Paginación
  page = 0;
  size = 10;
  totalPages = 0;

  // Estado
  loading = false;
  errorMsg: string | null = null;

  constructor(
    public authService: AuthService,
    private tarjetaBusService: TarjetaBusService,
    private cdr: ChangeDetectorRef
  ) {
    this.username = this.authService.getUsername();
  }

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.username = this.authService.getUsername();
    });

    this.cargarTransacciones();
  }

  buscar() {
    this.page = 0;
    this.transacciones = [];
    this.cargarTransacciones();
  }


  cargarTransacciones() {
    this.loading = true;
    this.errorMsg = null;

    this.tarjetaBusService
      .listarTransacciones(
        this.page,
        this.size,
        this.estado,
        this.fechaDesde,
        this.fechaHasta
      )
      .subscribe({
        next: (data) => {
          this.transacciones = data.transacciones;
          this.totalPages = data.totalPages;
          this.loading = false;


          this.cdr.detectChanges();
        },
        error: () => {
          this.errorMsg = 'Error al cargar transacciones';
          this.loading = false;
        }
      });
  }

  limpiarFiltros() {
    this.estado = '';
    this.fechaDesde = '';
    this.fechaHasta = '';
    this.page = 0;
    this.cargarTransacciones();
  }

  anterior() {
    if (this.page > 0) {
      this.page--;
      this.cargarTransacciones();
    }
  }

  siguiente() {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.cargarTransacciones();
    }
  }

  logout() {
    this.authService.logout();
  }
}
