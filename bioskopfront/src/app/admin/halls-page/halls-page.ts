import { Component } from '@angular/core';
import { CommonModule, NgFor } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { HallService, HallDto } from '../../core/hall';

@Component({
  selector: 'app-halls-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgFor],
  template: `
    <h2>Sale</h2>

    <div class="grid">
      <section>
        <h3>Dodaj salu</h3>
        <form [formGroup]="form" (ngSubmit)="create()" class="form">
          <label>Naziv <input formControlName="name" placeholder="Sala 1" /></label>
          <button class="btn primary" type="submit" [disabled]="form.invalid || loading">
            {{ loading ? 'Snima...' : 'Dodaj' }}
          </button>
          <span class="err" *ngIf="err">{{ err }}</span>
        </form>

        <h3 class="mt">Generiši sedišta</h3>
        <form [formGroup]="genForm" (ngSubmit)="generate()" class="form">
          <label>Sala
            <select formControlName="hallId">
              <option *ngFor="let h of halls" [value]="h.id">{{ h.name || ('Sala ' + h.id) }}</option>
            </select>
          </label>
          <label>Redova <input type="number" formControlName="rows" /></label>
          <label>Sedišta po redu <input type="number" formControlName="seatsPerRow" /></label>
          <button class="btn ghost" type="submit">Generiši</button>
          <span class="ok" *ngIf="okGen">Generisano.</span>
        </form>
      </section>

      <section>
        <h3>Postojeće sale</h3>
        <ul class="list">
          <li *ngFor="let h of halls">
            <strong>{{ h.name || ('Sala ' + h.id) }}</strong>
            <span class="muted">#{{ h.id }}</span>
          </li>
        </ul>
      </section>
    </div>
  `,
  styles: [`
    .grid{display:grid;grid-template-columns:1fr 1fr;gap:20px}
    .form{display:grid;grid-template-columns:1fr;gap:10px;max-width:400px}
    input,select{background:#0f1117;border:1px solid rgba(255,255,255,.12);color:#fff;border-radius:8px;padding:8px}
    .btn{padding:8px 12px;border-radius:8px;border:1px solid #7c4dff;color:#fff;background:#7c4dff;cursor:pointer}
    .btn.ghost{background:transparent}
    .list{padding:0;margin:0;list-style:none}
    .list li{display:flex;justify-content:space-between;border-bottom:1px solid rgba(255,255,255,.08);padding:6px 0}
    .muted{color:#9aa3b2}
    .mt{margin-top:18px}
    .ok{color:#b7ffdd}
    .err{color:#ffb4b4}
  `]
})
export class HallsPageComponent {
  halls: HallDto[] = [];
  loading = false; err = ''; okGen = false;
  form!: FormGroup;
  genForm!: FormGroup;

  constructor(private fb: FormBuilder, private hallsApi: HallService){
    this.form = this.fb.group({ name: ['', Validators.required] });
    this.genForm = this.fb.group({
      hallId: [null, Validators.required],
      rows: [10, [Validators.required, Validators.min(1)]],
      seatsPerRow: [12, [Validators.required, Validators.min(1)]],
    });
    this.load();
  }

  load(){ this.hallsApi.getAll().subscribe(h => this.halls = h); }

  create(){
    if (this.form.invalid) return;
    this.loading = true; this.err = '';
    const payload = this.form.getRawValue() as Pick<HallDto, 'name'>;
    this.hallsApi.create(payload).subscribe({
      next: () => { this.loading = false; this.form.reset(); this.load(); },
      error: e => { this.loading = false; this.err = typeof e?.error === 'string' ? e.error : 'Greška.'; }
    });
  }

  generate(){
    if (this.genForm.invalid) return;
    this.okGen = false;
    const { hallId, rows, seatsPerRow } = this.genForm.getRawValue() as { hallId: number; rows: number; seatsPerRow: number; };
    this.hallsApi.generateSeats(hallId, rows, seatsPerRow).subscribe({
      next: () => { this.okGen = true; },
      error: () => { this.okGen = false; }
    });
  }
}
