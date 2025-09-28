import { Component } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MovieDto, MovieService } from '../../core/movie';

@Component({
  selector: 'app-movies',
  standalone: true,
  imports: [CommonModule, RouterLink, NgIf, NgFor],
  template: `
    <section class="wrap">
      <h1>Filmovi</h1>

      <div class="grid" *ngIf="movies?.length; else empty">
        <article class="card" *ngFor="let m of movies">
          <div class="body">
            <h3 class="title">{{ m.title }}</h3>

            <p class="muted one-line" *ngIf="m.description">{{ m.description }}</p>

            <div class="meta">
              <span *ngIf="m.duration">{{ m.duration }} min</span>
              <span *ngIf="m.imdbRating !== undefined">IMDb: {{ m.imdbRating }}</span>
              <span *ngIf="m.rating !== undefined">Ocena: {{ m.rating }}</span>
            </div>

            <a class="btn primary" [routerLink]="['/movies', m.id]">Projekcije & kupovina</a>
          </div>
        </article>
      </div>

      <ng-template #empty>
        <p class="muted">Nema filmova.</p>
      </ng-template>
    </section>
  `,
  styles: [`
    .wrap{max-width:1100px;margin:24px auto;padding:0 16px;color:#e9ecf1;}
    h1{margin:0 0 12px;}
    .grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));gap:16px;}
    .card{background:#12131a;border:1px solid rgba(255,255,255,.06);border-radius:16px;overflow:hidden;display:flex;}
    .body{padding:14px 16px;display:flex;flex-direction:column;gap:10px;width:100%;}
    .title{margin:0;color:#fff;}
    .muted{color:#9aa3b2;margin:0;}
    .one-line{overflow:hidden;white-space:nowrap;text-overflow:ellipsis;}
    .meta{display:flex;gap:12px;flex-wrap:wrap;color:#cfd3dc;font-size:.9rem;}
    .btn.primary{align-self:flex-start;background:#7c4dff;color:#fff;border:none;border-radius:10px;padding:8px 12px;text-decoration:none;}
  `]
})
export class MoviesComponent {
  movies: MovieDto[] = [];

  constructor(private moviesApi: MovieService) {
    this.moviesApi.getAll().subscribe((res: MovieDto[]) => {
      this.movies = res;
    });
  }
}
