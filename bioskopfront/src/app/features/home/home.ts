import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgFor } from '@angular/common';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { FooterComponent } from '../../shared/footer/footer';
import { MovieCardComponent } from '../movies/movie-card/movie-card';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NavbarComponent, FooterComponent, MovieCardComponent, NgFor],
  template: `
    

    <section class="hero">
      <div class="container">
        <div class="text">
          <h1>Dobrodošli u Bioskop</h1>
          <p>Najbolji filmovi, besprekoran zvuk i najudobnija sedišta u gradu.
             Rezervišite karte online i preskočite red.</p>

          <div class="cta">
            <button class="btn primary" (click)="goToMoviesOrLogin()">Pogledaj repertoar</button>
            <button class="btn ghost" (click)="goToMoviesOrLogin()">Akcije i popusti</button>
          </div>

          <ul class="badges">
            <li>🍿 IMAX zvuk</li>
            <li>🎟️ Online rezervacije</li>
            <li>🕘 Late-night projekcije</li>
          </ul>
        </div>

        <div class="side-card">
          <div class="glass">
            <h3>Večeras u ponudi</h3>
            <p>Specijalne projekcije sa 20% popusta za studente.</p>
            <button class="btn white" (click)="goToMoviesOrLogin()">Rezerviši sada</button>
          </div>
        </div>
      </div>
    </section>

    <section class="grid">
      <div class="container">
        <div class="section-head">
          <h2>Top filmovi</h2>
          <a class="more" (click)="goToMoviesOrLogin()" href="javascript:void(0)">Vidi sve →</a>
        </div>

        <div class="cards">
          <app-movie-card *ngFor="let m of topMovies"
            [title]="m.title"
            [genre]="m.genre"
            [poster]="m.poster"
            [rating]="m.rating">
          </app-movie-card>
        </div>
      </div>
    </section>

    <app-footer></app-footer>
  `,
  styles: [`
    :host { display:block; background: radial-gradient(1200px 600px at 70% -10%, #7c4dff33, transparent), #0c0c10; color:#e9ecf1; }
    .container { max-width:1100px; margin:0 auto; padding:0 16px; }
    .btn { border-radius:12px; padding:10px 16px; border:1px solid transparent; cursor:pointer; }
    .btn.primary { background:#7c4dff; color:#fff; }
    .btn.ghost { background:transparent; border-color:#7c4dff; color:#fff; }
    .btn.white { background:#fff; color:#12131a; }
    .hero { padding:56px 0 34px; }
    .hero .container { display:grid; grid-template-columns:1.3fr .7fr; gap:24px; align-items:center; }
    .text h1 { font-size:2.4rem; margin:0 0 8px; }
    .text p { color:#cfd3dc; max-width:54ch; }
    .cta { display:flex; gap:12px; margin:16px 0 8px; }
    .badges { list-style:none; display:flex; gap:14px; padding:0; margin:10px 0 0; color:#b6bdd0; }
    .side-card .glass { border-radius:16px; padding:22px;
      background: linear-gradient(135deg, rgba(255,255,255,.08), rgba(255,255,255,.02)); border:1px solid rgba(255,255,255,.12); }
    .grid { padding:10px 0 64px; }
    .section-head { display:flex; justify-content:space-between; align-items:center; margin-bottom:14px; }
    .section-head h2 { margin:0; font-size:1.4rem; }
    .more { color:#98a1b3; text-decoration:none; cursor:pointer; }
    .more:hover { color:#fff; }
    .cards{ display:grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap:16px; align-items:stretch; }
    @media (max-width:900px){ .cards{ grid-template-columns: repeat(2, 1fr); } }
    @media (max-width:560px){ .cards{ grid-template-columns: 1fr; } }
  `]
})
export class HomeComponent {
  topMovies = [
    { title: 'Dune: Part Two', genre: 'SF, Avantura', rating: 8.7, poster: 'https://s3.amazonaws.com/nightjarprod/content/uploads/sites/261/2023/12/17144929/cBDoFHJVcZqAonkTyhN9sMEggi5-1-scaled.jpg' },
    { title: 'Oppenheimer', genre: 'Drama, Biografija', rating: 8.6, poster: 'https://upload.wikimedia.org/wikipedia/en/thumb/4/4a/Oppenheimer_%28film%29.jpg/250px-Oppenheimer_%28film%29.jpg' },
    { title: 'Inside Out 2', genre: 'Animirani', rating: 8.1, poster: 'https://upload.wikimedia.org/wikipedia/en/thumb/f/f7/Inside_Out_2_poster.jpg/250px-Inside_Out_2_poster.jpg' },
  ];

  constructor(private router: Router) {}

  /** Minimalna provera prijave – koristi token u Local/Session Storage.
   *  Ako imaš svoj AuthService, ovde pozovi npr. this.auth.isLoggedIn(). */
  private isLoggedIn(): boolean {
    const t = localStorage.getItem('token') ?? sessionStorage.getItem('token');
    return !!t && t.trim().length > 0;
  }

  /** Vodi na /movies ako je prijavljen, inače na /login */
  goToMoviesOrLogin(): void {
    this.router.navigate([ this.isLoggedIn() ? '/movies' : '/login' ]);
  }
}
