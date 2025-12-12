import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { AuthService } from "../../services/auth.service";
import { NavbarComponent } from "../navbar/navbar.component";
import { FormsModule } from "@angular/forms";

@Component({
  standalone: true,
  selector: 'app-actualizar-usuario-view',
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './actualizar-usuario.component.html',
  styleUrls: ['./actualizar-usuario.component.css']
})
export class ActualizarUsuarioComponent implements OnInit {

  isLoggedIn = false;
  username = '';

  usuario: any = {
    id: 1,
    nombre: '',
    apepa: '',
    apema: '',
    dni: '',
    email: '',
    pwd: ''
  };

  roles = [
    { id: 1, nombre: 'ADMIN' },
    { id: 2, nombre: 'CLIENTE' }
  ];

  tarjetas = [
    { id: 1, numTarjeta: '1234567890123456', saldo: 50 },
    { id: 2, numTarjeta: '9876543210987654', saldo: 80 }
  ];

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.username = this.authService.getUsername();
    });
  }

  guardar() {
    console.log('Datos enviados:', this.usuario);
    alert('Formulario listo para conectar al backend');
  }
}