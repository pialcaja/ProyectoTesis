import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError, tap } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);
  const accessToken = authService.getAccessToken();

  let authReq = req;

  if (accessToken) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    });
  }

  return next(authReq).pipe(

    catchError((error: HttpErrorResponse) => {

      if (
        error.status === 401 &&
        !req.url.includes('/auth/refresh')
      ) {
        return authService.refreshToken().pipe(

          switchMap(newToken =>
            next(
              req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`
                }
              })
            )
          ),

          catchError(err => {
            authService.logout();
            return throwError(() => err);
          })
        );
      }

      return throwError(() => error);
    })
  );
};
