import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { AuthService } from "../../services/auth.service";
import { NavbarComponent } from "../navbar/navbar.component";

@Component({
  standalone: true,
  selector: 'app-actualizar-usuario-view',
  imports: [CommonModule, NavbarComponent],
  templateUrl: './actualizar-usuario.component.html',
})
export class ActualizarUsuarioComponent implements OnInit {

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