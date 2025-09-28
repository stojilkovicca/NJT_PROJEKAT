import { Component, Input } from '@angular/core';
import { NgIf, DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-movie-card',
  standalone: true,
  imports: [NgIf, DecimalPipe],   // <= DODATO
  template: `
    <article class="card">
      <div class="poster" [style.backgroundImage]="'url(' + poster + ')'"></div>
      <div class="body">
        <h3>{{ title }}</h3>
        <p class="muted">{{ genre }}</p>
        <div class="bottom">
          <span *ngIf="rating">★ {{ rating | number:'1.1-1' }}</span>
          <button class="btn small">Rezerviši</button>
        </div>
      </div>
    </article>
  `,
  styles: [`
    .card { background:#12131a; border:1px solid rgba(255,255,255,.06);
      border-radius:16px; overflow:hidden; display:flex; flex-direction:column;
      transition: transform .15s ease, box-shadow .15s ease; }
    .card:hover { transform: translateY(-3px); box-shadow:0 10px 30px rgba(0,0,0,.25); }
    .poster { padding-top:140%; background-size:cover; background-position:center; }
    .body { padding:12px 14px; }
    .body h3 { margin:0 0 4px; font-size:1rem; color:#fff; }
    .muted { margin:0 0 10px; color:#98a1b3; font-size:.9rem; }
    .bottom { display:flex; justify-content:space-between; align-items:center; color:#ffd166; }
    .btn.small { padding:6px 10px; font-size:.85rem; border-radius:10px; border:1px solid #7c4dff; background:transparent; color:#fff; }
  `]
})
export class MovieCardComponent {
  @Input() title = '';
  @Input() genre = '';
  @Input() poster = '';
  @Input() rating?: number;
}
