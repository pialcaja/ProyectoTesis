import { Component } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";
import { NavbarComponent } from "../navbar/navbar.component";
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NavbarComponent
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      nombre: ['', Validators.required],
      apepa: ['', Validators.required],
      apema: ['', Validators.required],
      dni: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      pwd: ['', Validators.required]
    });
  }

submit() {
  if (this.form.invalid) {
    Swal.fire({
      icon: 'warning',
      title: 'Formulario inválido',
      text: 'Completa correctamente todos los campos obligatorios',
      confirmButtonText: 'Aceptar'
    });

    this.form.markAllAsTouched();
    return;
  }

  this.auth.register(this.form.value).subscribe({
    next: () => {
      Swal.fire({
        icon: 'success',
        title: 'Registro exitoso',
        text: 'Tu cuenta fue creada correctamente',
        confirmButtonText: 'Ir al login'
      }).then(() => {
        this.router.navigate(['/login']);
      });
    },
    error: () => {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Ocurrió un error al registrar el usuario',
        confirmButtonText: 'Aceptar'
      });
    }
  });
}

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
