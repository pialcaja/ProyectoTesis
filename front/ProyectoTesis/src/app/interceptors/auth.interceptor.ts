import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError, tap } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);
  const accessToken = authService.getAccessToken();

  console.log('➡️ Request:', req.method, req.url);

  let authReq = req;

  if (accessToken) {
    console.log('🔐 Access token enviado:', accessToken.substring(0, 15) + '...');
    authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${accessToken}` }
    });
  } else {
    console.log('⚠️ No hay access token');
  }

  return next(authReq).pipe(

    tap(event => {
      // Solo para ver que la request pasa OK
      console.log('✅ Response OK de:', req.url);
    }),

    catchError((error: HttpErrorResponse) => {

      console.error('❌ Error HTTP:', error.status, 'en', req.url);

      if (
        error.status === 401 &&
        !req.url.includes('/auth/refresh')
      ) {
        console.warn('🔄 Access token expirado → intentando refresh');

        return authService.refreshToken().pipe(

          tap(() => {
            console.log('♻️ Refresh token OK, nuevo access token guardado');
          }),

          switchMap(newToken => {
            console.log('🔁 Reintentando request original con nuevo token');

            return next(
              req.clone({
                setHeaders: { Authorization: `Bearer ${newToken}` }
              })
            );
          }),

          catchError(err => {
            console.error('🚫 Refresh token FALLÓ → logout');
            authService.logout();
            return throwError(() => err);
          })
        );
      }

      return throwError(() => error);
    })
  );
};
