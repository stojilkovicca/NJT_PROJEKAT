import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  template: `
    <section class="wrap">
      <header class="bar">
        <h1>Admin panel</h1>
        <nav class="tabs">
        <a routerLink="/admin/reservations">Rezervacije</a>
          <a routerLink="/admin/movies">Filmovi</a>
          <a routerLink="/admin/projections">Projekcije</a>
          <a routerLink="/admin/halls">Sale</a>
           
        </nav>
      </header>

      <router-outlet></router-outlet>
    </section>
  `,
  styles: [`
    .wrap{max-width:1100px;margin:18px auto;padding:0 16px;color:#e9ecf1}
    .bar{display:flex;justify-content:space-between;align-items:center;margin-bottom:12px}
    .tabs{display:flex;gap:10px}
    .tabs a{padding:6px 10px;border:1px solid rgba(255,255,255,.15);border-radius:8px;text-decoration:none;color:#cfd3dc}
    .tabs a:hover{color:#fff;border-color:#7c4dff}
  `]
})
export class AdminLayoutComponent {}
