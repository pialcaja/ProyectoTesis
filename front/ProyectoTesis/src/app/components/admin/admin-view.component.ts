import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  standalone: true,
  selector: 'app-admin-view',
  imports: [CommonModule],
  templateUrl: './admin-view.component.html',
  styleUrls: ['./admin-view.component.css']
})
export class AdminViewComponent {
  
  selected: string = 'rutas';

  constructor(private auth: AuthService, private router: Router) {}

  logout() {
    this.auth.logout();
  }

  seleccionar(op: string) {
    this.selected = op;
  }
}
