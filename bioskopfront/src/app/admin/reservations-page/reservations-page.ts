import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule, NgFor, NgIf, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ReservationService, ReservationDto } from '../../core/reservation';
import { ProjectionService, ProjectionDto } from '../../core/projection';
import { SeatService, SeatDto } from '../../core/seat';
import { HallService, HallDto } from '../../core/hall';
import { MovieService, MovieDto } from '../../core/movie';
import jsPDF from 'jspdf';
import { TicketDto, TicketService } from '../../core/ticket';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-reservations-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgFor, NgIf, NgClass],
  template: `
    <section class="admin">
      <div class="card">
        <h1>Rezervacije — admin</h1>
        <p class="muted">Pregled, filtriranje i izdavanje karata (PDF).</p>

        <!-- FILTERI -->
        <form [formGroup]="filters" (ngSubmit)="applyFilters()" class="filters">
          <input formControlName="q" placeholder="Pretraga (korisnik, film...)" />
          <label class="dates">
            Od
            <input type="datetime-local" formControlName="from"/>
          </label>
          <label class="dates">
            Do
            <input type="datetime-local" formControlName="to"/>
          </label>
          <label>
            Projekcija
            <select formControlName="projectionId">
              <option [ngValue]="null">Sve</option>
              <option *ngFor="let p of projections()" [ngValue]="p.id">
                #{{p.id}} — {{ movieTitle(p.movieId) }} @ {{ dateLabel(p.dateTime) }}
              </option>
            </select>
          </label>
          <button class="btn">Primeni</button>
          <button type="button" class="btn" (click)="resetFilters()">Reset</button>
        </form>

        <!-- LISTA -->
        <div class="list-head">
          <div>ID</div>
          <div>Korisnik</div>
          <div>Projekcija</div>
          <div>Br. karata</div>
          <div>Rezervisano</div>
          <div>Akcije</div>
        </div>
        <ul class="list">
          <li *ngFor="let r of filtered()" class="row">
            <div>#{{r.id}}</div>
            <div>{{ userName(r.userId) }}</div>
            <div>
              #{{ r.projectionId }}
              <span class="muted">— {{ movieTitle(projection(r.projectionId)?.movieId) }}</span>
              <div class="muted small">{{ dateLabel(projection(r.projectionId)?.dateTime) }}</div>
            </div>
            <div>{{ r.numberOfTickets }}</div>
            <div>{{ dateTimeLocal(r.reservedAt) }}</div>
            <div class="actions sm">
              <button class="btn" (click)="openIssueModal(r)" [disabled]="busy()">Izdaj kartu</button>
              <button class="btn danger" (click)="deleteReservation(r)" [disabled]="busy()">Obriši</button>
            </div>
          </li>
        </ul>

        <div *ngIf="err()" class="err">{{ err() }}</div>
      </div>
    </section>

    <!-- MODAL: izdavanje karata (više sedišta) -->
    <div class="modal-backdrop" *ngIf="issueOpen()" (click)="closeIssue()"></div>
    <div class="modal" *ngIf="issueOpen()" role="dialog" aria-modal="true">
      <div class="modal-head">
        <h3>Izdavanje karata — Rez #{{ currentRes()?.id }}</h3>
        <button class="icon-btn" (click)="closeIssue()" aria-label="Zatvori">✕</button>
      </div>

      <div *ngIf="loadingSeats()">Učitavam raspored...</div>

      <div *ngIf="!loadingSeats() && currentProj() as p">
        <div class="meta">
          <div><b>Film:</b> {{ movieTitle(p.movieId) }}</div>
          <div><b>Termin:</b> {{ dateLabel(p.dateTime) }}</div>
          <div><b>Sala:</b> {{ hall(p.hallId)?.name }} (cap {{ hall(p.hallId)?.capacity }})</div>
        </div>

        <form [formGroup]="issueForm" class="issue-form" (ngSubmit)="issueTickets()">
          <label>
            Cena (po karti, RSD)
            <input type="number" formControlName="price" min="0"/>
          </label>

          <div class="hint muted">
            Dozvoljeno sedišta: <b>{{ currentRes()?.numberOfTickets }}</b> ·
            Izabrano: <b>{{ selectedSeatIds().length }}</b>
          </div>

          <input type="hidden" formControlName="seatIds" />
          <div class="err" *ngIf="priceInvalid()">Unesi cenu (≥ 0).</div>
          <div class="err" *ngIf="seatInvalid()">Izaberi bar jedno sedište (maks. {{ currentRes()?.numberOfTickets }}).</div>

          <div class="seat-wrap" *ngIf="gridReady()">
            <div class="row-labels">
              <div *ngFor="let r of range(maxRow())" class="rlabel">R{{ r }}</div>
            </div>
            <div class="seat-grid" [style.--cols]="maxCol()">
              <button
                *ngFor="let cell of flatGrid(); let i = index"
                type="button"
                class="seat"
                [class.empty]="!cell"
                [class.taken]="cell && taken(cell.id)"
                [class.sel]="cell && isSelected(cell.id)"
                [disabled]="!cell || taken(cell.id) || (reachedLimit() && !isSelected(cell.id))"
                (click)="cell && toggleSeat(cell.id)">
                {{ cell?.label || '' }}
              </button>
            </div>
          </div>

          <div class="actions">
            <button
              class="btn primary"
              type="submit"
              [disabled]="!canSubmit() || busy()"
            >
              Izdaj {{ selectedSeatIds().length }} {{ selectedSeatIds().length === 1 ? 'kartu' : 'karte' }} (PDF)
            </button>
          </div>
        </form>

        <div class="muted small" *ngIf="selectedSeatIds().length > 0 && selectedSeatIds().length < (currentRes()?.numberOfTickets || 0)">
          Napomena: Izabrali ste manje sedišta od rezervisanog broja; izdaće se {{ selectedSeatIds().length }} karata.
        </div>
      </div>

      <div *ngIf="errIssue()" class="err mt">{{ errIssue() }}</div>
    </div>
  `,
  styles: [`
    .admin { min-height: calc(100dvh - 120px); display:grid; place-items:start center; padding:32px 20px; background:#0c0c10; }
    .card { width:100%; max-width:1100px; border:1px solid rgba(255,255,255,.08); background:#12131a; border-radius:16px; padding:24px; box-shadow:0 10px 30px rgba(0,0,0,.25); }
    h1{ margin:0 0 6px; color:#fff; font-size:1.6rem; }
    .muted{ color:#9aa3b2; } .small{ font-size:.92rem; }

    .filters{ display:grid; gap:10px; grid-template-columns: 1.2fr .9fr .9fr 1.4fr auto auto; margin:10px 0 16px; }
    .filters input, .filters select{ background:#0f1117; border:1px solid rgba(255,255,255,.1); color:#fff; border-radius:10px; padding:10px; }
    .dates{ display:flex; gap:6px; align-items:center; color:#cfd3dc; }

    .list-head, .list .row{ display:grid; grid-template-columns: 80px 1fr 2fr 120px 200px 220px; gap:10px; align-items:center; }
    .list-head{ color:#9aa3b2; padding:6px 0; border-bottom:1px solid rgba(255,255,255,.08); margin-top:6px; }
    .list{ list-style:none; padding:0; margin:0; }
    .list .row{ padding:10px 0; border-bottom:1px solid rgba(255,255,255,.08); }
    .actions{ display:flex; gap:10px; }
    .actions.sm{ gap:8px; }
    .btn{ padding:10px 14px; border-radius:10px; border:1px solid transparent; cursor:pointer; background:#1a1b23; color:#e6e6e6; }
    .btn.primary{ background:#7c4dff; color:#fff; }
    .btn.danger{ background:#2a1518; border-color:rgba(255,0,0,.25); color:#ffb4b4; }
    .err{ color:#ffb4b4; }

    .modal-backdrop{ position:fixed; inset:0; background:rgba(0,0,0,.6); }
    .modal{ position:fixed; inset:auto 0 0 0; margin:auto; top:6vh; max-width:1000px; width:calc(100% - 32px); background:#12131a; border:1px solid rgba(255,255,255,.08); border-radius:16px; padding:16px; box-shadow:0 20px 60px rgba(0,0,0,.45); z-index:50; }
    .modal-head{ display:flex; align-items:center; justify-content:space-between; margin-bottom:12px; }
    .icon-btn{ background:#1a1b23; border:1px solid rgba(255,255,255,.08); color:#e8e8ea; border-radius:10px; padding:6px 10px; cursor:pointer; }
    .meta{ display:flex; gap:16px; margin:6px 0 12px; color:#cfd3dc; }

    .issue-form{ display:grid; gap:10px; }
    .issue-form input{ background:#0f1117; border:1px solid rgba(255,255,255,.1); color:#fff; border-radius:10px; padding:10px; }

    .seat-wrap{ display:grid; grid-template-columns: auto 1fr; gap:10px; align-items:start; }
    .row-labels{ display:grid; gap:6px; }
    .rlabel{ color:#9aa3b2; font-size:.9rem; padding:4px 0; text-align:right; min-width:48px; }
    .seat-grid{ --cols: 10; display:grid; gap:6px; grid-template-columns: repeat(var(--cols), minmax(48px, 1fr)); }
    .seat{ background:#0f1117; border:1px solid rgba(255,255,255,.12); color:#cfd3dc; border-radius:10px; padding:8px 6px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; font-size:.85rem; cursor:pointer; }
    .seat.empty{ opacity:.25; border-style:dashed; cursor:default; }
    .seat.taken{ opacity:.45; text-decoration: line-through; cursor:not-allowed; }
    .seat.sel{ border-color:#7c4dff; box-shadow:0 0 0 3px rgba(124,77,255,.18); }

    @media (max-width: 980px){
      .list-head, .list .row{ grid-template-columns: 60px 1fr 1.7fr 90px 160px 200px; }
      .seat-grid{ grid-template-columns: repeat(var(--cols), minmax(36px, 1fr)); }
      .filters{ grid-template-columns: 1fr 1fr 1fr 1fr auto auto; }
    }
  `]
})
export class ReservationsPageComponent {
  private fb = inject(FormBuilder);
  private reservationsApi = inject(ReservationService);
  private projectionsApi = inject(ProjectionService);
  private ticketsApi = inject(TicketService);
  private seatsApi = inject(SeatService);
  private hallsApi = inject(HallService);
  private moviesApi = inject(MovieService);

  // data
  reservations = signal<ReservationDto[]>([]);
  projections = signal<ProjectionDto[]>([]);
  movies = signal<MovieDto[]>([]);
  halls = signal<HallDto[]>([]);
  err = signal<string>('');
  busy = signal<boolean>(false);

  // filters
  filters!: FormGroup<{
    q: FormControl<string | null>;
    from: FormControl<string | null>;
    to: FormControl<string | null>;
    projectionId: FormControl<number | null>;
  }>;

  // issue modal state
  issueOpen = signal(false);
  currentRes = signal<ReservationDto | null>(null);
  currentProj = signal<ProjectionDto | null>(null);
  loadingSeats = signal<boolean>(false);
  seats = signal<SeatDto[]>([]);
  takenTickets = signal<TicketDto[]>([]);
  maxRow = signal(0);
  maxCol = signal(0);
  flatGrid = signal<(SeatDto | null)[]>([]);
  gridReady = signal(false);
  errIssue = signal('');

  // višestruki izbor sedišta
  selectedSeatIds = signal<number[]>([]);
  reachedLimit = computed(() => {
    const limit = this.currentRes()?.numberOfTickets ?? 0;
    return this.selectedSeatIds().length >= limit && limit > 0;
  });

  // price + seatIds form
  issueForm!: FormGroup<{
    price: FormControl<number | null>;
    seatIds: FormControl<number[] | null>;
  }>;

  constructor(){
    // filters
    this.filters = this.fb.group({
      q: this.fb.control<string | null>(null),
      from: this.fb.control<string | null>(null),
      to: this.fb.control<string | null>(null),
      projectionId: this.fb.control<number | null>(null)
    });

    // issue form (višesedišno)
    this.issueForm = this.fb.group({
      price: this.fb.control<number | null>(null, [Validators.required, Validators.min(0)]),
      seatIds: this.fb.control<number[] | null>(null, [Validators.required]),
    });

    this.load();
  }

  // ---------- LOAD ----------
  load(){
    this.err.set('');
    this.reservationsApi.getAll().subscribe({
      next: rs => this.reservations.set(rs ?? []),
      error: () => this.err.set('Greška pri učitavanju rezervacija.')
    });
    this.projectionsApi.getAll().subscribe(p => this.projections.set(p ?? []));
    this.moviesApi.getAll().subscribe(m => this.movies.set(m ?? []));
    this.hallsApi.getAll().subscribe(h => this.halls.set(h ?? []));
  }

  // ---------- Helpers ----------
  projection(id?: number | null){ return this.projections().find(p => p.id === id!); }
  movieTitle(movieId?: number | null){
    return this.movies().find(m => m.id === movieId!)?.title || '—';
  }
  hall(id?: number | null){ return this.halls().find(h => h.id === id!); }

  dateLabel(iso?: string | null){
    if (!iso) return '';
    const d = new Date(iso);
    return `${d.toLocaleDateString()} ${d.toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}`;
  }
  dateTimeLocal(iso?: string | null){
    if (!iso) return '';
    const d = new Date(iso);
    return `${d.toLocaleDateString()} ${d.toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}`;
  }

  // ---------- Filter ----------
  applyFilters(){ /* computed handles it */ }
  resetFilters(){ this.filters.reset({ q: null, from: null, to: null, projectionId: null }); }

  filtered = computed(() => {
    const q = (this.filters.value.q || '').toLowerCase().trim();
    const pid = this.filters.value.projectionId;
    const from = this.filters.value.from ? new Date(this.filters.value.from) : null;
    const to   = this.filters.value.to ? new Date(this.filters.value.to) : null;

    return (this.reservations() || []).filter(r => {
      if (pid && r.projectionId !== pid) return false;

      if (from || to) {
        const t = r.reservedAt ? new Date(r.reservedAt) : null;
        if (from && t && t < from) return false;
        if (to && t && t > to) return false;
      }

      if (q) {
        const p = this.projection(r.projectionId);
        const movie = p ? this.movieTitle(p.movieId) : '';
        const user = this.userName(r.userId);
        const idtxt = `#${r.id}`;
        const hay = `${user} ${movie} ${idtxt}`.toLowerCase();
        if (!hay.includes(q)) return false;
      }
      return true;
    });
  });

  userName(userId: number){ return `user:${userId}`; }

  // ---------- Issue flow ----------
  openIssueModal(r: ReservationDto){
    this.errIssue.set('');
    this.issueOpen.set(true);
    this.currentRes.set(r);
    const p = this.projection(r.projectionId);
    this.currentProj.set(p || null);

    const base = (p as any)?.basePrice ?? 0;
    this.issueForm.reset({ price: base, seatIds: null });
    this.selectedSeatIds.set([]);

    this.loadingSeats.set(true);
    this.seatsApi.byHall(p!.hallId).subscribe({
      next: seats => {
        this.seats.set(seats ?? []);
        this.ticketsApi.byProjection(p!.id).subscribe({
          next: t => {
            this.takenTickets.set(t ?? []);
            this.buildGrid();
            this.loadingSeats.set(false);
          },
          error: () => { this.takenTickets.set([]); this.buildGrid(); this.loadingSeats.set(false); }
        });
      },
      error: () => { this.seats.set([]); this.loadingSeats.set(false); }
    });
  }
  closeIssue(){
    this.issueOpen.set(false);
    this.currentRes.set(null);
    this.currentProj.set(null);
    this.seats.set([]); this.takenTickets.set([]); this.flatGrid.set([]); this.gridReady.set(false);
    this.selectedSeatIds.set([]);
  }

  taken(seatId: number){ return this.takenTickets().some(t => t.seatId === seatId); }
  isSelected(seatId: number){ return this.selectedSeatIds().includes(seatId); }

  toggleSeat(seatId: number){
    const limit = this.currentRes()?.numberOfTickets ?? 0;
    const arr = [...this.selectedSeatIds()];
    const idx = arr.indexOf(seatId);

    if (idx >= 0){
      arr.splice(idx, 1);
    } else {
      if (arr.length >= limit) return; // ne dozvoli više od limita
      arr.push(seatId);
    }

    this.selectedSeatIds.set(arr);
    this.issueForm.patchValue({ seatIds: arr.length ? arr : null });
  }

  buildGrid(){
    const seats = this.seats();
    const maxR = seats.reduce((m,s)=> Math.max(m, s.rowNumber), 0);
    const maxC = seats.reduce((m,s)=> Math.max(m, s.seatNumber), 0);
    this.maxRow.set(maxR); this.maxCol.set(maxC);

    const map = new Map<string, SeatDto>();
    seats.forEach(s => map.set(`${s.rowNumber}-${s.seatNumber}`, s));
    const total = maxR * maxC;
    const flat: (SeatDto|null)[] = Array.from({length: total}, (_, idx) => {
      const r = Math.floor(idx / maxC) + 1;
      const c = (idx % maxC) + 1;
      return map.get(`${r}-${c}`) ?? null;
    });
    this.flatGrid.set(flat);
    this.gridReady.set(true);
  }

  range(n: number){ return Array.from({length: n}, (_,i)=> i+1); }

  // ---------- Validacija UI ----------
  priceInvalid(){ return this.issueForm.controls.price.invalid && this.issueForm.controls.price.touched; }
  seatInvalid(){
    const seats = this.selectedSeatIds().length;
    const limit = this.currentRes()?.numberOfTickets ?? 0;
    return seats === 0 || seats > limit;
  }
  canSubmit(){
    return this.issueForm.controls.price.valid && !this.seatInvalid();
  }

  // ---------- Create tickets + PDFs (više komada) ----------
  async issueTickets(){
    if (!this.canSubmit() || !this.currentRes() || !this.currentProj()) return;

    this.busy.set(true); this.errIssue.set('');
    const res = this.currentRes()!;
    const p = this.currentProj()!;
    const price = Number(this.issueForm.value.price);
    const seatIds = this.selectedSeatIds();

    // redund. zaštita
    const limit = res.numberOfTickets;
    if (seatIds.length === 0 || seatIds.length > limit){
      this.busy.set(false);
      this.errIssue.set(`Moraš izabrati između 1 i ${limit} sedišta.`);
      return;
    }

    try {
      // Izdaj jednu po jednu (da se ne sudaraju) i za svaku generiši PDF
      for (const seatId of seatIds){
        const dto = {
          ticketPrice: price,
          qrCode: null,
          projectionId: p.id,
          seatId,
          reservationId: res.id
        } as const;

        const saved = await firstValueFrom(this.ticketsApi.add(dto));
        await this.downloadPdfTicket(saved, p);
      }

      // Osvježi zauzeta sedišta i očisti izbor
      const t = await firstValueFrom(this.ticketsApi.byProjection(p.id));
      this.takenTickets.set(t ?? []);
      this.selectedSeatIds.set([]);
      this.issueForm.patchValue({ seatIds: null });

      this.busy.set(false);
    } catch (e: any){
      this.busy.set(false);
      this.errIssue.set(typeof e?.error === 'string' ? e.error : 'Greška pri izdavanju jedne ili više karata.');
    }
  }

  private async downloadPdfTicket(t: TicketDto, p: ProjectionDto){
    const seat = this.seats().find(s => s.id === t.seatId);

    const doc = new jsPDF();
    doc.setFontSize(16);
    doc.text('Karta za projekciju', 14, 18);

    doc.setFontSize(12);
    const y0 = 28;
    doc.text(`Karta ID: ${t.id}`, 14, y0);
    doc.text(`Projekcija: #${p.id}`, 14, y0+7);
    doc.text(`Film: ${this.movieTitle(p.movieId)}`, 14, y0+14);
    doc.text(`Sala: ${this.hall(p.hallId)?.name}`, 14, y0+21);
    doc.text(`Termin: ${this.dateLabel(p.dateTime)}`, 14, y0+28);
    doc.text(`Mesto: ${seat?.label || ('ID ' + t.seatId)}`, 14, y0+35);
    doc.text(`Cena: ${t.ticketPrice.toFixed(2)} RSD`, 14, y0+42);

    doc.save(`karta-${t.id}.pdf`);
  }

  // ---------- Actions ----------
  deleteReservation(r: ReservationDto){
    if (!confirm(`Obrisati rezervaciju #${r.id}?`)) return;
    this.busy.set(true); this.err.set('');
    this.reservationsApi.delete(r.id).subscribe({
      next: () => { this.busy.set(false); this.load(); },
      error: e => { this.busy.set(false); this.err.set(typeof e?.error === 'string' ? e.error : 'Greška pri brisanju.'); }
    });
  }
}
