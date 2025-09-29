import { Component } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { MovieService, MovieDto } from '../../core/movie';
import { GenreService, GenreDto } from '../../core/genre';

@Component({
  selector: 'app-movie-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgFor],
  template: `
    <section class="admin">
      <div class="card">
        <h1>Novi film</h1>
        <p class="muted">Popuni podatke i sačuvaj film u bazi.</p>

        <form [formGroup]="form" (ngSubmit)="save()" class="form-grid">
          <label>
            Naslov
            <input formControlName="title" placeholder="npr. Interstellar" />
          </label>

          <label>
            Trajanje (min)
            <input type="number" formControlName="duration" placeholder="npr. 169" />
          </label>

          <label class="col-span-2">
            Opis
            <textarea formControlName="description" rows="4" placeholder="Kratak sinopsis filma..."></textarea>
          </label>

          <label>
            IMDb
            <input type="number" step="0.1" formControlName="imdbRating" placeholder="npr. 8.6" />
          </label>

          <label>
            Ocena
            <input type="number" step="0.1" formControlName="rating" placeholder="npr. 9.2" />
          </label>

          <label>
            Producent
            <input formControlName="producer" placeholder="npr. Emma Thomas" />
          </label>

          <label>
            Glumci
            <input formControlName="actors" placeholder="npr. Matthew McConaughey, Anne Hathaway" />
          </label>

          <label>
            Žanr
            <select formControlName="genreId">
              <option [ngValue]="null" disabled>— izaberi žanr —</option>
              <option *ngFor="let g of genres" [ngValue]="g.id">{{ g.name }}</option>
            </select>
          </label>

          <div class="actions col-span-2">
            <button class="btn primary" type="submit" [disabled]="form.invalid || loading">
              {{ loading ? 'Snima...' : 'Sačuvaj' }}
            </button>
          </div>

          <p class="err col-span-2" *ngIf="err">{{ err }}</p>
        </form>
      </div>
    </section>
  `,
  styles: [`
    /* Layout */
    .admin { min-height: calc(100dvh - 120px); display:grid; place-items:start center; padding:32px 20px; background:#0c0c10; }
    .card {
      width:100%; max-width:900px; border:1px solid rgba(255,255,255,.08);
      background:#12131a; border-radius:16px; padding:24px;
      box-shadow:0 10px 30px rgba(0,0,0,.25);
    }

    h1{ margin:0 0 6px; color:#fff; font-size:1.6rem; }
    .muted{ color:#9aa3b2; margin:0 0 18px; }

    /* Grid */
    .form-grid{
      display:grid; gap:14px;
      grid-template-columns: 1fr 1fr;
    }
    .col-span-2{ grid-column: 1 / -1; }

    /* Fields */
    label{ display:flex; flex-direction:column; gap:6px; color:#cfd3dc; font-size:.95rem; }
    input, textarea, select{
      background:#0f1117; border:1px solid rgba(255,255,255,.1); color:#fff;
      border-radius:12px; padding:12px; outline:none; font-size:.98rem;
      transition: border-color .15s ease, box-shadow .15s ease, transform .05s ease;
    }
    textarea{ resize:vertical; min-height:96px; }
    input:focus, textarea:focus, select:focus{
      border-color:#7c4dff; box-shadow:0 0 0 3px rgba(124,77,255,.18);
    }
    input:hover, textarea:hover, select:hover{ border-color: rgba(255,255,255,.18); }
    input:active{ transform: scale(0.998); }

    /* Number inputs clean look */
    input[type=number]::-webkit-outer-spin-button,
    input[type=number]::-webkit-inner-spin-button{ -webkit-appearance: none; margin: 0; }
    input[type=number]{ -moz-appearance:textfield; }

    /* Actions */
    .actions{ display:flex; justify-content:flex-end; margin-top:6px; }
    .btn{
      padding:12px 18px; border-radius:12px; border:1px solid transparent; cursor:pointer;
      background:#1a1b23; color:#e6e6e6;
    }
    .btn.primary{ background:#7c4dff; color:#fff; }
    .btn.primary[disabled]{ opacity:.65; cursor:not-allowed; }

    /* Errors */
    .err{ color:#ffb4b4; font-size:.92rem; }

    /* Responsive */
    @media (max-width: 720px){
      .form-grid{ grid-template-columns: 1fr; }
      .actions{ justify-content:stretch; }
      .btn{ width:100%; }
    }
  `]
})
export class MovieFormComponent {
  loading = false;
  err = '';
  genres: GenreDto[] = [];

  form!: FormGroup<{
    title:       FormControl<string>;
    description: FormControl<string | null>;
    duration:    FormControl<number | null>;
    imdbRating:  FormControl<number | null>;
    rating:      FormControl<number | null>;
    producer:    FormControl<string | null>;
    actors:      FormControl<string | null>;
    genreId:     FormControl<number | null>;
  }>;

  constructor(
    private fb: FormBuilder,
    private api: MovieService,
    private genresApi: GenreService,
    private router: Router
  ){
    this.form = this.fb.group({
      title:       this.fb.nonNullable.control('', Validators.required),
      description: this.fb.control<string | null>(null),
      duration:    this.fb.control<number | null>(null),
      imdbRating:  this.fb.control<number | null>(null),
      rating:      this.fb.control<number | null>(null),
      producer:    this.fb.control<string | null>(null),
      actors:      this.fb.control<string | null>(null),
      genreId:     this.fb.control<number | null>(null, { validators: Validators.required }),
    });

    this.genresApi.getAll().subscribe(list => this.genres = list);
  }

  private toPayload(): Omit<MovieDto, 'id'> {
    const r = this.form.getRawValue();
    return {
      title: r.title.trim(),
      description: r.description?.trim() || undefined,
      duration: r.duration ?? undefined,
      imdbRating: r.imdbRating ?? undefined,
      rating: r.rating ?? undefined,
      producer: r.producer?.trim() || undefined,
      actors: r.actors?.trim() || undefined,
      genreId: r.genreId ?? undefined,
    };
  }

  save() {
    if (this.form.invalid) return;
    this.loading = true; this.err = '';

    const payload = this.toPayload();
    this.api.create(payload).subscribe({
      next: () => this.router.navigateByUrl('/admin/movies'),
      error: e => {
        this.loading = false;
        this.err = typeof e?.error === 'string' ? e.error : 'Greška pri snimanju.';
      }
    });
  }
}
