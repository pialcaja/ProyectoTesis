import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  standalone: true,
  selector: 'app-admin-view',
  imports: [CommonModule],
  template: `
    <h1>Vista del Admin</h1>
    <p>Bienvenido administrador.</p>

    <button (click)="logout()">Cerrar sesión</button>
  `
})
export class AdminViewComponent {
  constructor(private auth: AuthService, private router: Router) {}

  logout() {
    this.auth.logout();
  }
}
