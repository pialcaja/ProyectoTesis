import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [CommonModule, FormsModule]
})
export class LoginComponent {

  email = '';
  pwd = '';
  errorMsg = '';

  constructor(
    private auth: AuthService,
    private router: Router
  ) { }

  login() {
    this.auth.login({ email: this.email, pwd: this.pwd }).subscribe({
      next: () => {
        const role = this.auth.getRole();

        if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/buscar']);
        }
      },
      error: () => this.errorMsg = 'Credenciales inválidas'
    });
  }

}
