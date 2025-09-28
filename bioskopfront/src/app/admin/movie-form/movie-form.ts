
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
    <h2>Novi film</h2>

    <form [formGroup]="form" (ngSubmit)="save()" class="form">
      <label>Naslov <input formControlName="title" /></label>
      <label>Opis <textarea formControlName="description"></textarea></label>
      <label>Trajanje (min) <input type="number" formControlName="duration" /></label>
      <label>IMDb <input type="number" step="0.1" formControlName="imdbRating" /></label>
      <label>Ocena <input type="number" step="0.1" formControlName="rating" /></label>
      <label>Producent <input formControlName="producer" /></label>
      <label>Glumci <input formControlName="actors" /></label>

      <label>Žanr
        <select formControlName="genreId">
          <option [ngValue]="null" disabled>— izaberi žanr —</option>
          <option *ngFor="let g of genres" [ngValue]="g.id">{{ g.name }}</option>
        </select>
      </label>

      <div class="actions">
        <button class="btn primary" type="submit" [disabled]="form.invalid || loading">
          {{ loading ? 'Snima...' : 'Sačuvaj' }}
        </button>
      </div>

      <p class="err" *ngIf="err">{{ err }}</p>
    </form>
  `,
  styles: [`
    .form{display:grid;grid-template-columns:1fr;gap:10px;max-width:560px}
    input,textarea,select{background:#0f1117;border:1px solid rgba(255,255,255,.12);color:#fff;border-radius:8px;padding:8px}
    .actions{margin-top:8px}
    .btn{padding:8px 12px;border-radius:8px;border:1px solid transparent;cursor:pointer}
    .btn.primary{background:#7c4dff;color:#fff}
    .err{color:#ffb4b4}
  `]
})
export class MovieFormComponent {
  loading = false;
  err = '';
  genres: GenreDto[] = [];

  // 1) Samo DEKLARIŠEMO formu ovde…
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
    // 2) …a PRAVIMO je u konstruktoru (posle što je fb “ubrizgan”)
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
