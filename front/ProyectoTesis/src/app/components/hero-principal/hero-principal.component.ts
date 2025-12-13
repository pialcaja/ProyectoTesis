import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-hero-principal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero-principal.component.html',
  styleUrls: ['./hero-principal.component.css']
})
export class HeroPrincipalComponent {

  constructor(private auth: AuthService, private router: Router) {}

  onRecargar() {
    if (this.auth.isLogged()) {
      this.router.navigate(['/recargar-tarjeta']);
    } else {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/recargar-tarjeta' } });
    }
  }

}
