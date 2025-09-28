import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface RegisterRequest { username: string; email: string; password: string; }
export interface LoginRequest    { username: string; password: string; }
export interface LoginResponse   { token: string; role: string; username: string; userId?: number; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = 'http://localhost:8080/api/auth';
  constructor(private http: HttpClient) {}

  register(req: RegisterRequest) { return this.http.post<LoginResponse>(`${this.base}/register`, req); }
  login(req: LoginRequest)       { return this.http.post<LoginResponse>(`${this.base}/login`, req); }

  storeAuth(res: LoginResponse) {
    sessionStorage.setItem('jwt', res.token);
    sessionStorage.setItem('role', res.role);
    sessionStorage.setItem('username', res.username);
    if (res.userId != null) sessionStorage.setItem('userId', String(res.userId));
  }

  /** koristimo je u Navbaru */
  clearAuth() {
    sessionStorage.removeItem('jwt');
    sessionStorage.removeItem('role');
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('userId');
  }

  logout() { this.clearAuth(); }

  // getters
  get isLoggedIn(): boolean { return !!sessionStorage.getItem('jwt'); }
  get username(): string | null { return sessionStorage.getItem('username'); }
  get role(): string | null { return sessionStorage.getItem('role'); }
  get isAdmin(): boolean { return this.role === 'ADMIN'; }
  get userId(): number | null {
    const v = sessionStorage.getItem('userId'); return v ? Number(v) : null;
  }
}
