import { Component, Input } from '@angular/core';
import { NgIf, DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-movie-card',
  standalone: true,
  imports: [NgIf, DecimalPipe],
  template: `
    <article class="card">
      <img
        class="poster"
        [src]="poster"
        [alt]="title"
        loading="lazy"
        width="500"
        height="750"
      />
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
  .card{
    background:#12131a;border:1px solid rgba(255,255,255,.06);
    border-radius:16px;overflow:hidden;display:flex;flex-direction:column;
    transition:transform .15s, box-shadow .15s; height:100%;
  }
  .card:hover{ transform:translateY(-3px); box-shadow:0 10px 30px rgba(0,0,0,.25); }

  /* 2:3 ratio postera i uvek ista visina */
  .poster{
    width:100%;
    aspect-ratio:2/3;          /* svi posteri iste proporcije */
    object-fit:cover;
    display:block;background:#0f1117;
  }

  .body{
    padding:12px 14px;
    display:flex; flex-direction:column; gap:8px; /* da se lako gura dno */
    min-height: 120px;                              /* stabilna visina teksta */
  }
  .body h3{margin:0;font-size:1rem;color:#fff;}
  .muted{margin:0;color:#98a1b3;font-size:.9rem;}
  .bottom{margin-top:auto; display:flex; justify-content:space-between; align-items:center; color:#ffd166;}
  .btn.small{
    padding:6px 10px;font-size:.85rem;border-radius:10px;
    border:1px solid #7c4dff;background:transparent;color:#fff;
  }
`]

})
export class MovieCardComponent {
  @Input() title = '';
  @Input() genre = '';
  @Input() poster = '';   // npr. 'assets/posters/dune2.jpg' (bez vodeće /)
  @Input() rating?: number;
}
