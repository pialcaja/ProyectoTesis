import { Injectable } from '@angular/core';
import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
    HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, switchMap, catchError } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    private isRefreshing = false;

    constructor(private authService: AuthService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        console.log("📩 Interceptando:", req.url);

        const accessToken = this.authService.getAccessToken();
        let authReq = req;

        if (accessToken) {
            authReq = req.clone({
                setHeaders: { Authorization: `Bearer ${accessToken}` }
            });
            console.log("🔐 Usando Access Token:", accessToken.substring(0, 15) + "...");
        }

        return next.handle(authReq).pipe(
            catchError((error: HttpErrorResponse) => {

                console.log("⚠️ ERROR INTERCEPTADO:", error);


                if (error.status === 401 && !this.isRefreshing) {

                    this.isRefreshing = true;

                    return this.authService.refreshToken().pipe(
                        switchMap((newToken: string) => {
                            this.isRefreshing = false;

                            const retryReq = req.clone({
                                setHeaders: { Authorization: `Bearer ${newToken}` }
                            });

                            return next.handle(retryReq);
                        }),
                        catchError(err => {
                            this.isRefreshing = false;
                            this.authService.logout();
                            return throwError(() => err);
                        })
                    );
                }

                return throwError(() => error);
            })
        );
    }
}
