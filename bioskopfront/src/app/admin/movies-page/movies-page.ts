import { Component } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MovieService, MovieDto } from '../../core/movie';

@Component({
  selector: 'app-admin-movies-page',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, RouterLink],
  template: `
    <div class="section">
      <div class="head">
        <h2>Filmovi</h2>
        <a class="btn primary" routerLink="/admin/movies/new">+ Novi film</a>
      </div>

      <table class="tbl" *ngIf="movies.length; else empty">
        <thead>
          <tr><th>ID</th><th>Naslov</th><th>Trajanje</th><th>IMDb</th><th></th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let m of movies">
            <td>{{m.id}}</td>
            <td>{{m.title}}</td>
            <td>{{m.duration || '—'}}</td>
            <td>{{m.imdbRating || '—'}}</td>
            <td class="right">
              <a class="btn ghost" [routerLink]="['/movies', m.id]" target="_blank">Otvori</a>
              <button class="btn danger" (click)="remove(m.id)">Obriši</button>
            </td>
          </tr>
        </tbody>
      </table>
      <ng-template #empty><p class="muted">Nema filmova.</p></ng-template>
    </div>
  `,
  styles: [`
    .head{display:flex;justify-content:space-between;align-items:center;margin:10px 0}
    .btn{padding:8px 12px;border-radius:8px;border:1px solid rgba(255,255,255,.15);background:transparent;color:#e9ecf1;cursor:pointer}
    .btn.primary{border-color:#7c4dff;background:#7c4dff;color:#fff}
    .btn.ghost{border-color:#7c4dff}
    .btn.danger{border-color:#c74a4a;color:#ffd0d0}
    .tbl{width:100%;border-collapse:collapse}
    th,td{padding:8px;border-bottom:1px solid rgba(255,255,255,.08)}
    .right{text-align:right}
    .muted{color:#9aa3b2}
  `]
})
export class MoviesPageComponent {
  movies: MovieDto[] = [];
  constructor(private api: MovieService) { this.load(); }
  load(){ this.api.getAll().subscribe(list => this.movies = list); }
  remove(id: number){
    if(!confirm('Obrisati film?')) return;
    this.api.delete(id).subscribe(() => this.load());
  }
}
