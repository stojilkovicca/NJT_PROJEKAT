import { Component } from '@angular/core';
import { CommonModule, NgFor, NgIf, NgClass } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  Validators,
  FormGroup,
  FormControl,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import { HallService, HallDto, HallCreate } from '../../core/hall';
import { SeatService, SeatDto } from '../../core/seat';

@Component({
  selector: 'app-halls-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgFor, NgIf, NgClass],
  template: `
    <section class="admin">
      <div class="card">
        <h1>Sale</h1>
        <p class="muted">Dodaj salu i (opciono) automatski generiši sedišta.</p>

        <div class="grid">
          <!-- LEVO: forme -->
          <section>
            <h3>Dodaj salu</h3>
            <form [formGroup]="form" (ngSubmit)="create()" class="form">
              <label>
                Kapacitet
                <input type="number" formControlName="capacity" min="1" placeholder="npr. 100" />
              </label>

              <label>
                Naziv
                <input formControlName="name" placeholder="Sala 1" />
              </label>

              <div class="row2">
                <label>
                  Redova (opciono)
                  <input type="number" formControlName="rows" min="1" placeholder="ostavi prazno za null" />
                </label>
                <label>
                  Sedišta po redu (opciono)
                  <input type="number" formControlName="seatsPerRow" min="1" placeholder="ostavi prazno za null" />
                </label>
              </div>

              <div class="err" *ngIf="form.errors?.['gridExceedsCapacity']">
                Mreža je veća od kapaciteta ({{gridProduct(form)}} > {{form.value.capacity}}).
              </div>

              <div class="actions">
                <button class="btn primary" type="submit" [disabled]="form.invalid || form.errors || loading">
                  {{ loading ? 'Snima...' : 'Dodaj' }}
                </button>
                <span class="err" *ngIf="err">{{ err }}</span>
              </div>
            </form>

            <h3 class="mt">Generiši sedišta</h3>
            <form [formGroup]="genForm" (ngSubmit)="generate()" class="form">
              <label>
                Sala
                <select formControlName="hallId" (change)="onGenHallChange()">
                  <option [ngValue]="null" disabled>— izaberi salu —</option>
                  <option *ngFor="let h of halls" [ngValue]="h.id">
                    {{ h.name || ('Sala ' + h.id) }} (cap {{ h.capacity }})
                  </option>
                </select>
              </label>

              <div class="row2">
                <label>
                  Redova
                  <input type="number" formControlName="rows" min="1" />
                </label>
                <label>
                  Sedišta po redu
                  <input type="number" formControlName="seatsPerRow" min="1" />
                </label>
              </div>

              <div class="hint muted" *ngIf="genCap !== null">
                Maksimalno {{ genCap }} sedišta u ovoj sali.
              </div>
              <div class="err" *ngIf="genForm.errors?.['gridExceedsCapacity']">
                Mreža prelazi kapacitet ({{gridProduct(genForm)}} > {{genCap}}).
              </div>

              <div class="actions">
                <button class="btn" type="submit" [disabled]="genForm.invalid || genForm.errors">Generiši</button>
                <span class="ok" *ngIf="okGen">✅ Generisano.</span>
              </div>
            </form>
          </section>

          <!-- DESNO: lista -->
          <section>
            <h3>Postojeće sale</h3>
            <ul class="list">
              <li *ngFor="let h of halls" class="row">
                <div class="lh clickable" (click)="openSeatMap(h)">
                  <strong>{{ h.name || ('Sala ' + h.id) }}</strong>
                  <span class="muted">#{{ h.id }}</span>
                  <div class="muted small">
                    cap {{ h.capacity }}
                    <span *ngIf="h.rows && h.seatsPerRow">• {{h.rows}}×{{h.seatsPerRow}}</span>
                  </div>
                </div>

                <div class="actions sm">
                  <button
                    class="btn"
                    (click)="clearSeats(h); $event.stopPropagation()"
                    [disabled]="busyId===h.id">
                    🗑️ Raspored
                  </button>
                  <button
                    class="btn danger"
                    (click)="deleteHall(h); $event.stopPropagation()"
                    [disabled]="busyId===h.id">
                    🗑️ Sala
                  </button>
                </div>
              </li>
            </ul>
            <p class="muted small">Klikni na salu da vidiš raspored sedišta.</p>
          </section>
        </div>
      </div>
    </section>

    <!-- MODAL: seat map -->
    <div class="modal-backdrop" *ngIf="seatModalOpen" (click)="closeSeatMap()"></div>
    <div class="modal" *ngIf="seatModalOpen" role="dialog" aria-modal="true">
      <div class="modal-head">
        <h3>Raspored — {{ selectedHall?.name || ('Sala ' + selectedHall?.id) }}</h3>
        <div class="head-actions">
          <button class="btn" (click)="selectedHall && clearSeats(selectedHall)" [disabled]="busyId===selectedHall?.id">🗑️ Obriši raspored</button>
          <button class="icon-btn" (click)="closeSeatMap()" aria-label="Zatvori">✕</button>
        </div>
      </div>

      <div *ngIf="seatsLoading" class="muted">Učitavam sedišta...</div>
      <div *ngIf="!seatsLoading && seats.length === 0" class="muted">Nema generisanih sedišta.</div>

      <div *ngIf="!seatsLoading && seats.length > 0" class="seat-wrap">
        <!-- redne oznake -->
        <div class="row-labels">
          <div *ngFor="let r of range(maxRow)" class="rlabel">R{{ r }}</div>
        </div>

        <!-- grid sedišta -->
        <div class="seat-grid" [style.--cols]="maxCol">
          <button
            *ngFor="let cell of flatGrid"
            class="seat"
            [class.empty]="!cell"
            [disabled]="!cell">
            {{ cell?.label || '' }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    /* Layout & card */
    .admin { min-height: calc(100dvh - 120px); display:grid; place-items:start center; padding:32px 20px; background:#0c0c10; }
    .card {
      width:100%; max-width:1000px; border:1px solid rgba(255,255,255,.08);
      background:#12131a; border-radius:16px; padding:24px;
      box-shadow:0 10px 30px rgba(0,0,0,.25);
    }
    h1{ margin:0 0 6px; color:#fff; font-size:1.6rem; }
    h3{ color:#e8e8ea; margin:14px 0 10px; }
    .muted{ color:#9aa3b2; }
    .small{ font-size:.92rem; }

    .grid{ display:grid; gap:20px; grid-template-columns: 1fr 1fr; }
    .form{ display:grid; gap:12px; }
    .row2{ display:grid; gap:12px; grid-template-columns: 1fr 1fr; }

    label{ display:flex; flex-direction:column; gap:6px; color:#cfd3dc; font-size:.95rem; }
    input, select{
      background:#0f1117; border:1px solid rgba(255,255,255,.1); color:#fff;
      border-radius:12px; padding:12px; outline:none; font-size:.98rem;
      transition: border-color .15s ease, box-shadow .15s ease, transform .05s ease;
    }
    input:focus, select:focus{ border-color:#7c4dff; box-shadow:0 0 0 3px rgba(124,77,255,.18); }

    .actions{ display:flex; align-items:center; gap:12px; }
    .actions.sm { gap:8px; }
    .btn{ padding:12px 18px; border-radius:12px; border:1px solid transparent; cursor:pointer; background:#1a1b23; color:#e6e6e6; }
    .btn.primary{ background:#7c4dff; color:#fff; }
    .btn.danger{ background:#2a1518; border-color:rgba(255,0,0,.25); color:#ffb4b4; }
    .ok{ color:#b7ffdd; }
    .err{ color:#ffb4b4; }

    .list{ padding:0; margin:0; list-style:none; }
    .list .row{ display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid rgba(255,255,255,.08); padding:10px 0; }
    .clickable{ cursor:pointer; }
    .lh { display:flex; align-items:center; gap:10px; }
    .lh strong{ color:#fff; }

    /* Modal */
    .modal-backdrop{ position:fixed; inset:0; background:rgba(0,0,0,.6); backdrop-filter: blur(2px); }
    .modal{
      position:fixed; inset:auto 0 0 0; margin:auto; top:8vh; max-width:900px; width:calc(100% - 32px);
      background:#12131a; border:1px solid rgba(255,255,255,.08); border-radius:16px; padding:16px;
      box-shadow:0 20px 60px rgba(0,0,0,.45); z-index:50;
    }
    .modal-head{ display:flex; align-items:center; justify-content:space-between; margin-bottom:12px; gap:10px; }
    .head-actions{ display:flex; align-items:center; gap:8px; }
    .icon-btn{ background:#1a1b23; border:1px solid rgba(255,255,255,.08); color:#e8e8ea; border-radius:10px; padding:6px 10px; cursor:pointer; }

    /* Seat grid */
    .seat-wrap{ display:grid; grid-template-columns: auto 1fr; gap:10px; align-items:start; }
    .row-labels{ display:grid; gap:6px; }
    .rlabel{ color:#9aa3b2; font-size:.9rem; padding:4px 0; text-align:right; min-width:48px; }
    .seat-grid{
      --cols: 10;
      display:grid; gap:6px; grid-template-columns: repeat(var(--cols), minmax(48px, 1fr));
    }
    .seat{
      background:#0f1117; border:1px solid rgba(255,255,255,.12); color:#cfd3dc; border-radius:10px; padding:8px 6px;
      white-space:nowrap; overflow:hidden; text-overflow:ellipsis; font-size:.85rem;
    }
    .seat.empty{ opacity:.25; border-style:dashed; }

    @media (max-width: 900px){
      .grid{ grid-template-columns: 1fr; }
      .seat-grid{ grid-template-columns: repeat(var(--cols), minmax(36px, 1fr)); }
    }
  `]
})
export class HallsPageComponent {
  halls: HallDto[] = [];
  loading = false; err = ''; okGen = false;

  form!: FormGroup<{
    name: FormControl<string>;
    capacity: FormControl<number>;
    rows: FormControl<number | null>;
    seatsPerRow: FormControl<number | null>;
  }>;

  genForm!: FormGroup<{
    hallId: FormControl<number | null>;
    rows: FormControl<number | null>;
    seatsPerRow: FormControl<number | null>;
  }>;

  genCap: number | null = null;

  // modal state
  seatModalOpen = false;
  selectedHall: HallDto | null = null;
  seats: SeatDto[] = [];
  seatsLoading = false;
  maxRow = 0;
  maxCol = 0;
  flatGrid: (SeatDto | null)[] = [];

  // busy indikator za dugmad po hali
  busyId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private hallsApi: HallService,
    private seatApi: SeatService
  ){
    this.form = this.fb.group({
      name: this.fb.nonNullable.control('', [Validators.required, Validators.maxLength(80)]),
      capacity: this.fb.nonNullable.control(100, [Validators.required, Validators.min(1)]),
      rows: this.fb.control<number | null>(null, [Validators.min(1)]),
      seatsPerRow: this.fb.control<number | null>(null, [Validators.min(1)]),
    }, { validators: this.gridWithinCapacityValidator });

    this.genForm = this.fb.group({
      hallId: this.fb.control<number | null>(null, Validators.required),
      rows: this.fb.control<number | null>(10, [Validators.required, Validators.min(1)]),
      seatsPerRow: this.fb.control<number | null>(12, [Validators.required, Validators.min(1)]),
    }, { validators: this.genGridWithinCapacityValidator });

    this.load();
  }

  // ---- VALIDACIJE ----
  gridWithinCapacityValidator = (group: AbstractControl): ValidationErrors | null => {
    const g = group as FormGroup;
    const capacity = Number(g.get('capacity')?.value ?? 0);
    const rows = Number(g.get('rows')?.value ?? 0);
    const perRow = Number(g.get('seatsPerRow')?.value ?? 0);
    if (!rows || !perRow) return null; // opciono
    return rows * perRow <= capacity ? null : { gridExceedsCapacity: true };
  };

  genGridWithinCapacityValidator = (group: AbstractControl): ValidationErrors | null => {
    const g = group as FormGroup;
    const hallId = g.get('hallId')?.value as number | null;
    const rows = Number(g.get('rows')?.value ?? 0);
    const perRow = Number(g.get('seatsPerRow')?.value ?? 0);
    if (!hallId || !rows || !perRow) return null;
    const hall = this.halls.find(h => h.id === hallId);
    if (!hall) return null;
    const cap = Number(hall.capacity ?? 0);
    this.genCap = cap;
    return rows * perRow <= cap ? null : { gridExceedsCapacity: true };
  };

  gridProduct(ctrl: AbstractControl | null): number {
    if (!ctrl) return 0;
    const g = ctrl as FormGroup;
    const rows = Number(g.get('rows')?.value ?? 0);
    const perRow = Number(g.get('seatsPerRow')?.value ?? 0);
    return rows && perRow ? rows * perRow : 0;
  }

  // ---- DATA ----
  load(){ this.hallsApi.getAll().subscribe(h => this.halls = h); }

  private toNull(n: any): number | null {
    return n === '' || n === undefined || n === null ? null : Number(n);
  }

  // ---- CREATE ----
  create(){
    if (this.form.invalid || this.form.errors) return;
    this.loading = true; this.err = '';

    const v = this.form.getRawValue();
    const payload: HallCreate = {
      name: v.name.trim(),
      capacity: Number(v.capacity),
      rows: this.toNull(v.rows),
      seatsPerRow: this.toNull(v.seatsPerRow),
    };
    this.hallsApi.create(payload).subscribe({
      next: () => {
        this.loading = false;
        this.form.reset({ name: '', capacity: 100, rows: null, seatsPerRow: null });
        this.load();
      },
      error: e => {
        this.loading = false;
        this.err = typeof e?.error === 'string' ? e.error : 'Greška.';
      }
    });
  }

  // ---- GENERATE ----
  onGenHallChange(){
    this.genForm.updateValueAndValidity();
  }

  generate(){
    if (this.genForm.invalid || this.genForm.errors) return;
    this.okGen = false;
    const { hallId, rows, seatsPerRow } = this.genForm.getRawValue();
    this.hallsApi.generateSeats(hallId as number, rows as number, seatsPerRow as number).subscribe({
      next: () => { this.okGen = true; },
      error: () => { this.okGen = false; }
    });
  }

  // ---- MODAL & GRID ----
  openSeatMap(h: HallDto){
    this.selectedHall = h;
    this.seatModalOpen = true;
    this.seatsLoading = true;
    this.seatApi.byHall(h.id).subscribe({
      next: (list) => {
        this.seats = list ?? [];
        this.buildGrid();
        this.seatsLoading = false;
      },
      error: () => {
        this.seats = []; this.seatsLoading = false;
      }
    });
  }
  closeSeatMap(){ this.seatModalOpen = false; this.selectedHall = null; this.seats = []; }

  range(n: number){ return Array.from({length: n}, (_,i) => i+1); }

  private buildGrid(){
    this.maxRow = this.seats.reduce((m,s) => Math.max(m, s.rowNumber), 0);
    this.maxCol = this.seats.reduce((m,s) => Math.max(m, s.seatNumber), 0);
    const total = this.maxRow * this.maxCol;
    const map = new Map<string, SeatDto>();
    for (const s of this.seats) map.set(`${s.rowNumber}-${s.seatNumber}`, s);
    this.flatGrid = Array.from({length: total}, (_, idx) => {
      const r = Math.floor(idx / this.maxCol) + 1;
      const c = (idx % this.maxCol) + 1;
      return map.get(`${r}-${c}`) ?? null;
    });
  }

  // ---- DELETE OPS ----
  deleteHall(h: HallDto){
    if (!confirm(`Obrisati salu "${h.name || h.id}"?`)) return;
    this.busyId = h.id;
    this.err = '';
    this.hallsApi.delete(h.id).subscribe({
      next: () => {
        if (this.selectedHall?.id === h.id) this.closeSeatMap();
        this.busyId = null;
        this.load();
      },
      error: (e) => {
        this.busyId = null;
        this.err = typeof e?.error === 'string' ? e.error : 'Greška pri brisanju sale.';
      }
    });
  }

  clearSeats(h: HallDto){
    if (!confirm(`Obrisati raspored sedišta za salu "${h.name || h.id}"?`)) return;
    this.busyId = h.id;
    this.err = '';
    this.seatApi.clearHall(h.id).subscribe({
      next: () => {
        this.busyId = null;
        // refreš liste i modal
        this.load();
        if (this.selectedHall?.id === h.id) {
          this.seats = [];
          this.buildGrid();
        }
      },
      error: (e) => {
        this.busyId = null;
        this.err = typeof e?.error === 'string' ? e.error : 'Greška pri brisanju rasporeda.';
      }
    });
  }
}
