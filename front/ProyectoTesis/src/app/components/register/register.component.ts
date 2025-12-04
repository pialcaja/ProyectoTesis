import { Component } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";
import { Router } from "@angular/router";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
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
    if (this.form.valid) {
      this.auth.register(this.form.value).subscribe(() => {
        alert('Registro completado');
        this.router.navigate(['/login']);
      });
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
