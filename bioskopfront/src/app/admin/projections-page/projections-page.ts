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
    <section class="admin">
      <div class="card">
        <h1>Projekcije — dodavanje</h1>
        <p class="muted">Izaberi film i salu, zatim postavi termin i cenu.</p>

        <form [formGroup]="form" (ngSubmit)="save()" class="form-grid">
          <label>
            Film
            <select formControlName="movieId">
              <option [ngValue]="null" disabled>— izaberi film —</option>
              <option *ngFor="let m of movies" [ngValue]="m.id">{{ m.title }}</option>
            </select>
          </label>

          <label>
            Sala
            <select formControlName="hallId">
              <option [ngValue]="null" disabled>— izaberi salu —</option>
              <option *ngFor="let h of halls" [ngValue]="h.id">{{ h.name || ('Sala ' + h.id) }}</option>
            </select>
          </label>

          <label class="col-span-2">
            Datum & vreme
            <input type="datetime-local"
                   formControlName="localDateTime"
                   [min]="minLocal"
                   step="900" />
          </label>

          <label>
            Osnovna cena (RSD)
            <input type="number" formControlName="basePrice" placeholder="npr. 450" />
          </label>

          <div class="actions">
            <button class="btn primary" type="submit" [disabled]="form.invalid || loading">
              {{ loading ? 'Snima...' : 'Dodaj' }}
            </button>
          </div>

          <div class="msg">
            <span class="ok" *ngIf="ok">✅ Sačuvano.</span>
            <span class="err" *ngIf="err">{{ err }}</span>
          </div>
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
      align-items:end;
    }
    .col-span-2{ grid-column: 1 / -1; }

    /* Fields */
    label{ display:flex; flex-direction:column; gap:6px; color:#cfd3dc; font-size:.95rem; }
    select, input{
      background:#0f1117; border:1px solid rgba(255,255,255,.1); color:#fff;
      border-radius:12px; padding:12px; outline:none; font-size:.98rem;
      transition: border-color .15s ease, box-shadow .15s ease, transform .05s ease;
    }
    select:focus, input:focus{
      border-color:#7c4dff; box-shadow:0 0 0 3px rgba(124,77,255,.18);
    }
    select:hover, input:hover{ border-color: rgba(255,255,255,.18); }
    input:active{ transform: scale(0.998); }
    /* number look */
    input[type=number]::-webkit-outer-spin-button,
    input[type=number]::-webkit-inner-spin-button{ -webkit-appearance: none; margin: 0; }
    input[type=number]{ -moz-appearance:textfield; }

    /* Actions & messages */
    .actions{ display:flex; justify-content:flex-end; }
    .btn{
      padding:12px 18px; border-radius:12px; border:1px solid transparent; cursor:pointer;
      background:#1a1b23; color:#e6e6e6;
    }
    .btn.primary{ background:#7c4dff; color:#fff; }
    .btn.primary[disabled]{ opacity:.65; cursor:not-allowed; }

    .msg{ grid-column: 1 / -1; display:flex; gap:12px; align-items:center; min-height:28px; }
    .ok{ color:#b7ffdd; }
    .err{ color:#ffb4b4; }

    /* Responsive */
    @media (max-width: 720px){
      .form-grid{ grid-template-columns: 1fr; }
      .actions{ justify-content:stretch; }
      .btn{ width:100%; }
    }
  `]
})
export class ProjectionsPageComponent {
  movies: MovieDto[] = [];
  halls: HallDto[] = [];
  loading = false; ok = false; err = '';

  form!: FormGroup<{
    movieId:       FormControl<number | null>;
    hallId:        FormControl<number | null>;
    localDateTime: FormControl<string | null>;
    basePrice:     FormControl<number | null>;
  }>;

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
    const iso = new Date(v.localDateTime as string).toISOString().slice(0,19); // YYYY-MM-DDTHH:mm:ss

    const payload: Omit<ProjectionDto,'id'> = {
      movieId:   v.movieId as number,
      hallId:    v.hallId as number,
      dateTime:  iso,   // backend LocalDateTime
      basePrice: v.basePrice as number,
    } as any;

    this.proj.create(payload).subscribe({
      next: () => { this.loading = false; this.ok = true; },
      error: e => { this.loading = false; this.err = typeof e?.error === 'string' ? e.error : 'Greška.'; }
    });
  }

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
