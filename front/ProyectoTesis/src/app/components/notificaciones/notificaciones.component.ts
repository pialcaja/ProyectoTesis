import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { NavbarComponent } from "../navbar/navbar.component";
import { AuthService } from "../../services/auth.service";

@Component({
  standalone: true,
  selector: 'app-notificaciones-view',
  imports: [CommonModule, NavbarComponent],
  templateUrl: './notificaciones.component.html',
})
export class NotificacionesComponent implements OnInit {

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