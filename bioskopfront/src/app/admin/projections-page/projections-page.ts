// src/app/admin/projections-page/projections-page.ts
import { Component } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { ProjectionService, ProjectionDto } from '../../core/projection';
import { MovieService, MovieDto } from '../../core/movie';
import { HallService, HallDto } from '../../core/hall';

@Component({
  selector: 'app-projections-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgFor],
  template: `
    <h2>Projekcije — dodavanje</h2>

    <form [formGroup]="form" (ngSubmit)="save()" class="form">
      <label>Film
        <select formControlName="movieId">
          <option *ngFor="let m of movies" [value]="m.id">{{ m.title }}</option>
        </select>
      </label>

      <label>Sala
        <select formControlName="hallId">
          <option *ngFor="let h of halls" [value]="h.id">{{ h.name || ('Sala ' + h.id) }}</option>
        </select>
      </label>

      <label>Datum & vreme
        <input type="datetime-local"
               formControlName="localDateTime"
               [min]="minLocal"
               step="900" />
      </label>

      <label>Osnovna cena (RSD)
        <input type="number" formControlName="basePrice" />
      </label>

      <button class="btn primary" type="submit" [disabled]="form.invalid || loading">
        {{ loading ? 'Snima...' : 'Dodaj' }}
      </button>
      <span class="ok" *ngIf="ok">Sačuvano.</span>
      <span class="err" *ngIf="err">{{ err }}</span>
    </form>
  `,
  styles: [`
    .form{display:grid;grid-template-columns:1fr;gap:10px;max-width:520px}
    select,input{background:#0f1117;border:1px solid rgba(255,255,255,.12);color:#fff;border-radius:8px;padding:8px}
    .btn{padding:8px 12px;border-radius:8px;border:1px solid transparent;cursor:pointer}
    .btn.primary{background:#7c4dff;color:#fff}
    .ok{color:#b7ffdd;margin-left:8px}
    .err{color:#ffb4b4;margin-left:8px}
  `]
})
export class ProjectionsPageComponent {
  movies: MovieDto[] = [];
  halls: HallDto[] = [];
  loading = false; ok = false; err = '';

  // koristimo "datetime-local" kontrolu; čuvamo je kao string "YYYY-MM-DDTHH:mm"
  form!: FormGroup<{
    movieId:       FormControl<number | null>;
    hallId:        FormControl<number | null>;
    localDateTime: FormControl<string | null>;
    basePrice:     FormControl<number | null>;
  }>;

  // minimalno vreme = sada (u format za input[type=datetime-local])
  minLocal = this.toLocalInputValue(new Date());

  constructor(
    private fb: FormBuilder,
    private proj: ProjectionService,
    private moviesApi: MovieService,
    private hallsApi: HallService
  ){
    this.form = this.fb.group({
      movieId:       this.fb.control<number | null>(null, { validators: Validators.required }),
      hallId:        this.fb.control<number | null>(null, { validators: Validators.required }),
      localDateTime: this.fb.control<string | null>(this.roundToNextQuarterHour(new Date()), { validators: Validators.required }),
      basePrice:     this.fb.control<number | null>(450, { validators: [Validators.required, Validators.min(0)] }),
    });

    this.moviesApi.getAll().subscribe(m => this.movies = m);
    this.hallsApi.getAll().subscribe(h => this.halls = h);
  }

  save() {
    if (this.form.invalid) return;
    this.loading = true; this.ok = false; this.err = '';

    const v = this.form.getRawValue();

    // "datetime-local" vraća lokalni string "YYYY-MM-DDTHH:mm"
    // pretvaramo u ISO sa sekundama (backend LocalDateTime)
    const iso = new Date(v.localDateTime as string).toISOString().slice(0,19); // YYYY-MM-DDTHH:mm:ss

    const payload: Omit<ProjectionDto,'id'> = {
      movieId:   v.movieId as number,
      hallId:    v.hallId as number,
      dateTime:  iso,          // <- backend ga mapira u LocalDateTime
      basePrice: v.basePrice as number,
    } as any;

    this.proj.create(payload).subscribe({
      next: () => { this.loading = false; this.ok = true; },
      error: e => { this.loading = false; this.err = typeof e?.error === 'string' ? e.error : 'Greška.'; }
    });
  }

  // Helpers za formatiranje "datetime-local"
  private toLocalInputValue(d: Date): string {
    const pad = (n: number) => n.toString().padStart(2,'0');
    const yyyy = d.getFullYear();
    const mm = pad(d.getMonth()+1);
    const dd = pad(d.getDate());
    const hh = pad(d.getHours());
    const mi = pad(d.getMinutes());
    return `${yyyy}-${mm}-${dd}T${hh}:${mi}`;
  }
  private roundToNextQuarterHour(d: Date): string {
    const copy = new Date(d);
    const m = copy.getMinutes();
    const rounded = Math.ceil(m / 15) * 15;
    copy.setMinutes(rounded === 60 ? 0 : rounded, 0, 0);
    if (rounded === 60) copy.setHours(copy.getHours()+1);
    return this.toLocalInputValue(copy);
  }
}
