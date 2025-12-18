import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private API = 'http://localhost:8080/auth';

  private ACCESS_TOKEN = 'access_token';
  private REFRESH_TOKEN = 'refresh_token';
  private USERNAME = 'username';
  private USERID = 'user_id';
  private ROLE = 'role';

  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  login(data: any) {
    return this.http.post<any>(`${this.API}/login`, data).pipe(
      tap(resp => this.saveLoginData(resp))
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(`${this.API}/register`, data);
  }

  saveLoginData(resp: any) {
    localStorage.setItem(this.ACCESS_TOKEN, resp.token);
    localStorage.setItem(this.REFRESH_TOKEN, resp.refreshToken);
    localStorage.setItem(this.USERID, resp.id.toString());
    localStorage.setItem(this.USERNAME, resp.nombre);
    localStorage.setItem(this.ROLE, resp.rol);

    this.isLoggedInSubject.next(true);
  }

  hasToken(): boolean {
    return !!localStorage.getItem(this.ACCESS_TOKEN);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN);
  }

  getUsername(): string {
    return localStorage.getItem(this.USERNAME) || '';
  }

  getUserId(): number {
    return Number(localStorage.getItem(this.USERID));
  }

  getRole(): string {
    return localStorage.getItem(this.ROLE) || '';
  }

  hasRole(role: string): boolean {
    const storedRole = this.getRole();

    if (!storedRole) return false;

    if (storedRole.includes(',')) {
      const roles = storedRole.split(',').map(r => r.replace('ROLE_', '').trim().toUpperCase());
      return roles.includes(role.toUpperCase());
    }

    return storedRole.replace('ROLE_', '').toUpperCase() === role.toUpperCase();
  }

  isLogged(): boolean {
    return this.hasToken();
  }

  logout() {
    localStorage.clear();
    this.isLoggedInSubject.next(false);
    this.router.navigate(['/home']);
  }

  refreshToken() {
    const refresh = this.getRefreshToken();

    return this.http.post<any>(`${this.API}/refresh`, { refreshToken: refresh }).pipe(
      tap(resp => {
        localStorage.setItem(this.ACCESS_TOKEN, resp.token);
        localStorage.setItem(this.REFRESH_TOKEN, resp.refreshToken);
      }),
      map(resp => resp.token)
    );
  }
}
