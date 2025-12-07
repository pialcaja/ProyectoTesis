import { Routes } from '@angular/router';
import { AdminViewComponent } from './components/admin/admin-view.component';
import { AdminGuard } from './guards/admin.guard';
import { ClienteGuard } from './guards/cliente.guard';
import { BusquedaRutaComponent } from './components/busqueda-ruta/busqueda-ruta.component';
import { authGuard } from './guards/auth.guard';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { NosotrosComponent } from './components/nosotros/nosotros.component';
import { ConsultarSaldoComponent } from './components/consultar-saldo/consultar-saldo.component';
import { RecargarTarjetaComponent } from './components/recargar-tarjeta/recargar-tarjeta.component';
import { ContactoComponent } from './components/contacto/contacto.component';
import { ActualizarUsuarioComponent } from './components/actualizar-usuario/actualizar-usuario.component';
import { NotificacionesComponent } from './components/notificaciones/notificaciones.component';
import { TransaccionesComponent } from './components/transacciones/transacciones.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  { path: 'home', component: HomeComponent },

  { path: 'nosotros', component: NosotrosComponent },
  
  { path: 'contacto', component: ContactoComponent },
  
  { path: 'buscar', component: BusquedaRutaComponent, canActivate: [ClienteGuard] },
  
  { path: 'notificaciones', component: NotificacionesComponent, canActivate: [ClienteGuard] },

  { path: 'consultar-saldo', component: ConsultarSaldoComponent, canActivate: [ClienteGuard] },

  { path: 'recargar-tarjeta', component: RecargarTarjetaComponent, canActivate: [ClienteGuard] },

  { path: 'admin', component: AdminViewComponent, canActivate: [AdminGuard] },

  { path: 'login', loadComponent: () => 
      import('./components/login/login.component').then(m => m.LoginComponent)
  },

  { path: 'register', component: RegisterComponent },

  { path: 'actualizar-usuario', component: ActualizarUsuarioComponent, canActivate: [ClienteGuard] },
  
  { path: 'transacciones', component: TransaccionesComponent, canActivate: [ClienteGuard] },

];
