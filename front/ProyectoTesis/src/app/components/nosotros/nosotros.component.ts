import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { AuthService } from "../../services/auth.service";
import { NavbarComponent } from "../navbar/navbar.component";

@Component({
  standalone: true,
  selector: 'app-nosotros-view',
  imports: [CommonModule, NavbarComponent],
  templateUrl: './nosotros.component.html',
})
export class NosotrosComponent implements OnInit {

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