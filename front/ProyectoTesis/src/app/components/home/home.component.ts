import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CarruselHeroComponent } from '../carrusel-hero/carrusel-hero.component';
import { HeroPrincipalComponent } from '../hero-principal/hero-principal.component';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'home',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule,
    NavbarComponent,
    CarruselHeroComponent,
    HeroPrincipalComponent

  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  isLoggedIn = false;
  username = '';

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.username = this.authService.getUsername();
    });
  }

  logout() {
    this.authService.logout();
  }
}
