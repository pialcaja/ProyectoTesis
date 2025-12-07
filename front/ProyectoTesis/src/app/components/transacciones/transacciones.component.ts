import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { NavbarComponent } from "../navbar/navbar.component";
import { AuthService } from "../../services/auth.service";

@Component({
  standalone: true,
  selector: 'app-transacciones-view',
  imports: [CommonModule, NavbarComponent],
  templateUrl: './transacciones.component.html',
})
export class TransaccionesComponent implements OnInit {

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