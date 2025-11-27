import { Routes } from '@angular/router';
import { AdminViewComponent } from './components/admin-view.component';
import { AdminGuard } from './guards/admin.guard';
import { BusquedaRutaComponent } from './components/busqueda-ruta.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'buscar', pathMatch: 'full' },

  { path: 'buscar', component: BusquedaRutaComponent, canActivate: [authGuard] },

  { path: 'admin', component: AdminViewComponent, canActivate: [AdminGuard] },

  { path: 'login', loadComponent: () => 
      import('./components/login.component').then(m => m.LoginComponent)
  }
];
