import { Routes } from '@angular/router';
import { AdminViewComponent } from './components/admin/admin-view.component';
import { AdminGuard } from './guards/admin.guard';
import { ClienteGuard } from './guards/cliente.guard';
import { BusquedaRutaComponent } from './components/busqueda-ruta/busqueda-ruta.component';
import { authGuard } from './guards/auth.guard';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  { path: 'home', component: HomeComponent },
  
  { path: 'buscar', component: BusquedaRutaComponent, canActivate: [ClienteGuard] },

  { path: 'admin', component: AdminViewComponent, canActivate: [AdminGuard] },

  { path: 'login', loadComponent: () => 
      import('./components/login/login.component').then(m => m.LoginComponent)
  },

  { path: 'register', component: RegisterComponent }

];
