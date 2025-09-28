import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../core/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgIf],
  template: `
    <header class="nav">
      <div class="container">
        <a class="brand" routerLink="/">🎬 Bioskop</a>

        <nav class="links">
          <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{ exact:true }">Početna</a>
          <a routerLink="/movies" routerLinkActive="active">Filmovi</a>
          <a routerLink="/about" routerLinkActive="active">O nama</a>
        </nav>

        <div class="actions">
          <!-- Nije prijavljen -->
          <ng-container *ngIf="!isLoggedIn; else logged">
            <a class="btn ghost" routerLink="/login">Prijava</a>
            <a class="btn primary" routerLink="/register">Registracija</a>
          </ng-container>

          <!-- Prijavljen -->
          <ng-template #logged>
            <span class="user">👤 {{ username }}</span>
            <button class="btn ghost" (click)="logout()">Odjava</button>
          </ng-template>
        </div>
      </div>
    </header>
  `,
  styles: [`
    .nav { position: sticky; top:0; z-index:50; backdrop-filter: blur(8px);
      background: rgba(12,12,16,.6); border-bottom:1px solid rgba(255,255,255,.08); }
    .container { max-width:1100px; margin:0 auto; padding:10px 16px;
      display:grid; grid-template-columns:1fr auto auto; gap:16px; align-items:center; }
    .brand { font-weight:700; letter-spacing:.3px; text-decoration:none; color:#fff; font-size:1.1rem; }
    .links { display:flex; gap:16px; }
    .links a { color:#cfd3dc; text-decoration:none; font-weight:500; padding:6px 4px; border-bottom:2px solid transparent; }
    .links a.active { color:#fff; border-bottom-color:#7c4dff; }
    .actions { display:flex; gap:8px; align-items:center; }
    .user { color:#cfd3dc; margin-right:6px; }
    .btn { border-radius:999px; padding:8px 14px; border:1px solid transparent; cursor:pointer; }
    .btn.primary { background:#7c4dff; color:#fff; }
    .btn.ghost { background:transparent; border-color:#7c4dff; color:#fff; }
  `]
})
export class NavbarComponent {
  constructor(private auth: AuthService, private router: Router) {}

  get isLoggedIn() { return this.auth.isLoggedIn; }
  get username()   { return this.auth.username; }

  logout() {
    this.auth.clearAuth();
    this.router.navigateByUrl('/login');
  }
}
