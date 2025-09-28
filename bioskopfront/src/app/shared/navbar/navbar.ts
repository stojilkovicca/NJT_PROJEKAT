import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink],
  // umesto templateUrl
  template: `
    <header class="nav">
      <div class="container">
        <a class="brand" routerLink="/">🎬 Bioskop</a>
        <nav class="links">
          <a routerLink="/">Početna</a>
          <a routerLink="/movies">Filmovi</a>
          <a routerLink="/about">O nama</a>
        </nav>
        <div class="actions">
          <button class="btn ghost">Prijava</button>
          <button class="btn primary">Registracija</button>
        </div>
      </div>
    </header>
  `,
  // umesto styleUrls
  styles: [`
    .nav { position: sticky; top:0; z-index:50; backdrop-filter: blur(8px);
      background: rgba(12,12,16,.6); border-bottom:1px solid rgba(255,255,255,.08); }
    .container { max-width:1100px; margin:0 auto; padding:10px 16px;
      display:grid; grid-template-columns:1fr auto auto; gap:16px; align-items:center; }
    .brand { font-weight:700; letter-spacing:.3px; text-decoration:none; color:#fff; font-size:1.1rem; }
    .links { display:flex; gap:16px; }
    .links a { color:#cfd3dc; text-decoration:none; font-weight:500; }
    .links a:hover { color:#fff; }
    .actions { display:flex; gap:8px; }
    .btn { border-radius:999px; padding:8px 14px; border:1px solid transparent; cursor:pointer; }
    .btn.primary { background:#7c4dff; color:#fff; }
    .btn.ghost { background:transparent; border-color:#7c4dff; color:#fff; }
  `]
})
export class NavbarComponent {}
