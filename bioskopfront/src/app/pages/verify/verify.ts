import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/auth';

@Component({
  selector: 'app-verify',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="verify-wrap">
      <div class="card">
        <div class="header">
          <div class="badge">Verifikacija naloga</div>
          <h1>Provera e-mail adrese</h1>
          <p class="subtitle">Zatvaramo krug bezbednosti za tvoj nalog.</p>
        </div>

        <!-- LOADING -->
        <div *ngIf="loading" class="state loading">
          <div class="spinner"></div>
          <p>Provera tokena…</p>
        </div>

        <!-- SUCCESS -->
        <div *ngIf="!loading && ok" class="state success">
          <div class="icon success-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="40" height="40">
              <path d="M9 12.5l2 2 4-5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h2>E-mail uspešno verifikovan.</h2>
          <p class="muted">{{ ok }}</p>

          <div class="redirect">
            <div class="bar">
              <div class="bar-fill" [style.width.%]="progress"></div>
            </div>
            <p>Preusmeravam na prijavu za {{ countdown }}s…</p>
          </div>
        </div>

        <!-- ERROR -->
        <div *ngIf="!loading && err" class="state error">
          <div class="icon error-icon" aria-hidden="true">!</div>
          <h2>Verifikacija nije uspela</h2>
          <p class="muted">{{ err }}</p>
          <a class="link" routerLink="/resend-verification">Pošalji novi verifikacioni mejl</a>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .verify-wrap {
      min-height: calc(100dvh - 120px);
      display: grid; place-items: center;
      padding: 24px; background: #0c0c10;
    }
    .card {
      width: 100%; max-width: 680px;
      background: #12131a; border: 1px solid rgba(255,255,255,.08);
      border-radius: 18px; padding: 28px;
      box-shadow: 0 16px 40px rgba(0,0,0,.35);
      color: #e7e9ee;
    }
    .header { text-align: left; margin-bottom: 18px; }
    .badge {
      display: inline-block; font-size: .78rem; letter-spacing: .3px;
      padding: 6px 10px; border-radius: 999px; color: #cbbcff;
      background: rgba(124,77,255,.12); border: 1px solid rgba(124,77,255,.25);
      margin-bottom: 8px;
    }
    h1 { margin: 0 0 4px; font-size: 1.55rem; color: #fff; }
    .subtitle { margin: 0; color: #a3abbc; }

    .state { text-align: center; padding: 14px 6px 4px; }
    .state h2 { margin: 8px 0 6px; color: #fff; font-size: 1.25rem; }
    .muted { color: #a3abbc; }

    /* spinner */
    .spinner {
      width: 42px; height: 42px; border-radius: 50%;
      border: 3px solid rgba(255,255,255,.18);
      border-top-color: #7c4dff;
      margin: 14px auto 10px;
      animation: spin 1s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }

    .icon { display: grid; place-items: center; margin: 8px auto 6px; width: 48px; height: 48px; border-radius: 50%; }
    .success-icon { color: #7CFFB4; border: 2px solid rgba(124,255,180,.35); }
    .error-icon {
      background: rgba(255,82,82,.14); color: #ff7b7b;
      border: 2px solid rgba(255,82,82,.35);
      font-weight: 800; font-size: 1.2rem;
    }

    /* redirect bar */
    .redirect { margin-top: 14px; }
    .bar {
      width: 100%; height: 8px; background: rgba(255,255,255,.08);
      border-radius: 999px; overflow: hidden; margin: 8px 0 6px;
    }
    .bar-fill {
      height: 100%; background: linear-gradient(90deg, #7c4dff, #9c6bff);
      width: 0%; transition: width .2s ease;
    }

    .link { display:inline-block; margin-top: 10px; color:#cfd3dc; text-decoration: underline; }
  `]
})
export default class VerifyComponent implements OnDestroy {
  loading = true;
  ok = '';
  err = '';

  // redirect state
  countdown = 4;              // sekunde do redirect-a
  progress = 0;               // % za progress bar
  private tId: any = null;    // interval id

  constructor(route: ActivatedRoute, auth: AuthService, private router: Router) {
    const token = route.snapshot.queryParamMap.get('token') || '';
    if (!token) { this.err = 'Nedostaje token.'; this.loading = false; return; }

    auth.verify(token).subscribe({
      next: txt => {
        this.ok = typeof txt === 'string' ? txt : 'E-mail uspešno verifikovan.';
        this.loading = false;
        this.startRedirectTimer();
      },
      error: e => {
        this.err = e?.error || 'Verifikacija nije uspela.';
        this.loading = false;
      }
    });
  }

  private startRedirectTimer() {
    const total = this.countdown; // npr. 4s
    this.progress = 0;

    this.tId = setInterval(() => {
      this.countdown -= 1;
      const elapsed = total - this.countdown;
      this.progress = Math.min(100, Math.round((elapsed / total) * 100));

      if (this.countdown <= 0) {
        clearInterval(this.tId);
        this.router.navigate(['/login'], { queryParams: { verified: '1' } });
      }
    }, 1000);
  }

  ngOnDestroy() {
    if (this.tId) clearInterval(this.tId);
  }
}
