import { Component } from '@angular/core';
import { CommonModule, NgIf, NgFor, DecimalPipe, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { AuthService } from '../../core/auth';
import { MovieDto, MovieService } from '../../core/movie';
import { ProjectionDto, ProjectionService } from '../../core/projection';
import { PosterService } from '../../core/poster';
import { ReservationService } from '../../core/reservation';
 

@Component({
  selector: 'app-movie-details',
  standalone: true,
  imports: [CommonModule, RouterLink, NgIf, NgFor, DecimalPipe, DatePipe, FormsModule],
  template: `
    <section class="wrap" *ngIf="movie">
      <div class="head">
        <ng-container *ngIf="posterUrl; else noimg">
          <img class="poster" [src]="posterUrl!" [alt]="movie.title" />
        </ng-container>
        <ng-template #noimg>
          <div class="poster placeholder">Nema postera</div>
        </ng-template>

        <div class="meta">
          <h1>{{ movie.title }}</h1>

          <ul class="facts">
            <li *ngIf="movie.duration !== undefined">⏱️ Trajanje: {{ movie.duration }} min</li>
            <li *ngIf="movie.imdbRating !== undefined">⭐ IMDb: {{ movie.imdbRating }}</li>
            <li *ngIf="movie.rating !== undefined">💯 Ocena: {{ movie.rating }}</li>
            <li *ngIf="movie.genreId !== undefined">🎞️ Žanr (ID): {{ movie.genreId }}</li>
            <li *ngIf="movie.producer">🎬 Producent: {{ movie.producer }}</li>
            <li *ngIf="movie.actors">👥 Glumci: {{ movie.actors }}</li>
          </ul>

          <p class="muted" *ngIf="movie.description; else nodesc">
            {{ movie.description }}
          </p>
          <ng-template #nodesc><p class="muted">Nema opisa.</p></ng-template>
        </div>
      </div>

      <h2>Projekcije</h2>
      <div class="list" *ngIf="projections?.length; else noProj">
        <div class="item" *ngFor="let p of projections">
          <div class="info">
            <div class="line">
              <span>{{ p.dateTime | date:'EEEE, dd.MM.yyyy HH:mm' }}</span>
              <span>· Sala {{ p.hallId || '—' }}</span>
            </div>
            <div class="price">{{ (p.basePrice || 0) | number:'1.0-0' }} RSD</div>
          </div>

          <div class="buy">
            <label>
              Količina
              <input type="number" min="1" [(ngModel)]="counts[p.id]" />
            </label>
            <button class="btn primary" (click)="reserve(p)">Rezerviši</button>
          </div>
        </div>
      </div>
      <ng-template #noProj><p class="muted">Nema projekcija.</p></ng-template>

      <div class="msg ok" *ngIf="okMsg">{{ okMsg }}</div>
      <div class="msg err" *ngIf="errMsg">{{ errMsg }}</div>
    </section>
  `,
  styles: [`
    .wrap{max-width:1100px;margin:24px auto;padding:0 16px;color:#e9ecf1;}
    .head{display:grid;grid-template-columns:240px 1fr;gap:16px;margin-bottom:16px;}
    .poster{width:100%;aspect-ratio:2/3;object-fit:cover;background:#0f1117;border-radius:12px;}
    .poster.placeholder{display:grid;place-items:center;color:#9aa3b2;border:1px dashed rgba(255,255,255,.2);}
    .muted{color:#9aa3b2}
    .facts{list-style:none;padding:0;margin:8px 0 12px;display:flex;flex-direction:column;gap:6px;color:#cfd3dc;}
    h2{margin:18px 0 10px;}
    .list{display:flex;flex-direction:column;gap:10px;}
    .item{display:grid;grid-template-columns:1fr auto;gap:12px;align-items:center;
          border:1px solid rgba(255,255,255,.08);padding:12px;border-radius:12px;background:#12131a;}
    .line{display:flex;gap:8px;flex-wrap:wrap;}
    .price{color:#ffd166;}
    .buy{display:flex;gap:8px;align-items:center;}
    input[type="number"]{width:90px;background:#0f1117;border:1px solid rgba(255,255,255,.1);color:#fff;border-radius:8px;padding:6px 8px;}
    .btn.primary{background:#7c4dff;color:#fff;border:none;border-radius:10px;padding:8px 12px;cursor:pointer;}
    .msg{margin-top:12px;padding:10px 12px;border-radius:10px;}
    .msg.ok{background:#133b2e;color:#b7ffdd;border:1px solid #1f6a52;}
    .msg.err{background:#3b1313;color:#ffd0d0;border:1px solid #6a1f1f;}
    @media(max-width:680px){ .head{grid-template-columns:1fr;} }
  `]
})
export class MovieDetailsComponent {
  movie?: MovieDto;
  posterUrl: string | null = null;

  projections: ProjectionDto[] = [];
  counts: Record<number, number> = {};
  okMsg = '';
  errMsg = '';

  constructor(
    route: ActivatedRoute,
    private moviesApi: MovieService,
    private projApi: ProjectionService,
    private reservationApi: ReservationService,
    private posters: PosterService,
    private auth: AuthService
  ) {
    const id = Number(route.snapshot.paramMap.get('id'));

    // Film + poster
    this.moviesApi.getById(id).subscribe((m: MovieDto) => {
      this.movie = m;
      this.posters.getPoster(m.title).subscribe((url: string | null) => {
        this.posterUrl = url;
      });
    });

    // Projekcije
    this.projApi.getByMovie(id).subscribe((list: ProjectionDto[]) => {
      this.projections = list;
      list.forEach((p: ProjectionDto) => (this.counts[p.id] = 1));
    });
  }

  reserve(p: ProjectionDto) {
    this.okMsg = '';
    this.errMsg = '';

    if (!this.auth.isLoggedIn) { this.errMsg = 'Prijavi se da bi rezervisao karte.'; return; }

    const uid = this.auth.userId;
    if (!uid) { this.errMsg = 'Nedostaje userId u sesiji. Dodaj userId u LoginResponse ili obezbedi /api/users/me.'; return; }

    const qty = Math.max(1, Number(this.counts[p.id] ?? 1));

    this.reservationApi.create({
      numberOfTickets: qty,
      projectionId: p.id,
      userId: uid
    }).subscribe({
      next: () => this.okMsg = 'Rezervacija uspešna. Vidimo se u bioskopu! 🎬',
      error: (e: HttpErrorResponse) =>
        this.errMsg = typeof e?.error === 'string' ? e.error : 'Rezervacija nije uspela.'
    });
  }
}
