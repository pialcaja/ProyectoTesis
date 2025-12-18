import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import Swal from 'sweetalert2';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, FormsModule, NavbarComponent]
})
export class LoginComponent {

  email = '';
  pwd = '';
  errorMsg = '';

  private returnUrl: string | null = null;

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || null;
  }

  login() {
    this.auth.login({ email: this.email, pwd: this.pwd }).subscribe({
      next: () => {
        const role = this.auth.getRole();

        if (this.returnUrl) {
          this.router.navigateByUrl(this.returnUrl);
          return;
        }

        if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Credenciales inválidas',
          text: 'El correo o la contraseña no son correctos'
        });
      }
    });
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}

