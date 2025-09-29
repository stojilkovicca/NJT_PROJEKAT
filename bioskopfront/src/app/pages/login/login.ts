import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  Validators,
  FormGroup,
  FormControl
} from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { AuthService, LoginRequest } from '../../core/auth';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="auth">
      <div class="card">
        <h1>Prijava</h1>
        <p class="muted">Unesi podatke za prijavu.</p>

        <div class="success" *ngIf="registeredMsg">✅ Uspešno registrovan nalog. Prijavi se.</div>
        <div class="err" *ngIf="serverErr">{{ serverErr }}</div>

        <form [formGroup]="form" (ngSubmit)="submit()">
          <label>
            Korisničko ime
            <input type="text" formControlName="username" placeholder="npr. vanja" />
          </label>
          <div class="err" *ngIf="f.username.touched && f.username.invalid">
            {{ f.username.errors?.['required'] ? 'Korisničko ime je obavezno.' : '' }}
          </div>

          <label>
            Lozinka
            <input type="password" formControlName="password" placeholder="••••••••" />
          </label>
          <div class="err" *ngIf="f.password.touched && f.password.invalid">
            {{ f.password.errors?.['required'] ? 'Lozinka je obavezna.' : '' }}
          </div>

          <button class="btn primary" type="submit" [disabled]="form.invalid || loading">
            {{ loading ? 'Prijavljivanje...' : 'Prijava' }}
          </button>

          <p class="small">
            Nemaš nalog?
            <a routerLink="/register">Registruj se</a>
          </p>
        </form>
      </div>
    </section>
  `,
  styles: [`
    .auth { min-height: calc(100dvh - 120px); display:grid; place-items:center; padding: 24px; background:#0c0c10; }
    .card {
      width: 100%; max-width: 460px; border:1px solid rgba(255,255,255,.08);
      background:#12131a; border-radius:16px; padding:24px;
      box-shadow:0 10px 30px rgba(0,0,0,.25);
    }
    h1 { margin:0 0 6px; color:#fff; }
    .muted { color:#9aa3b2; margin:0 0 16px; }
    .success { background:#133b2e; color:#b7ffdd; border:1px solid #1f6a52; padding:8px 12px; border-radius:10px; margin-bottom:12px; }
    form { display:flex; flex-direction:column; gap:12px; }
    label { display:flex; flex-direction:column; gap:6px; color:#cfd3dc; font-size:.95rem; }
    input {
      background:#0f1117; border:1px solid rgba(255,255,255,.1); color:#fff;
      border-radius:10px; padding:10px 12px; outline:none;
    }
    input:focus { border-color:#7c4dff; box-shadow:0 0 0 3px rgba(124,77,255,.18); }
    .btn.primary { background:#7c4dff; color:#fff; border:none; border-radius:12px; padding:12px 16px; cursor:pointer; }
    .btn.primary[disabled]{ opacity:.6; cursor:not-allowed; }
    .err { color:#ffb4b4; font-size:.88rem; }
    .small { color:#9aa3b2; font-size:.92rem; }
    .small a { color:#cfd3dc; text-decoration:underline; }
  `]
})
export class LoginComponent implements OnDestroy {
  loading = false;
  serverErr = '';
  registeredMsg = false;
  qpSub?: Subscription;

  form!: FormGroup<{
    username: FormControl<string>;
    password: FormControl<string>;
  }>;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      username: this.fb.nonNullable.control('', [Validators.required]),
      password: this.fb.nonNullable.control('', [Validators.required]),
    });

    // poruka nakon registracije (?registered=1)
    this.qpSub = this.route.queryParamMap.subscribe(params => {
      this.registeredMsg = params.get('registered') === '1';
    });
  }

  get f() { return this.form.controls; }

  submit() {
    this.serverErr = '';
    if (this.form.invalid) return;

    this.loading = true;
    const payload: LoginRequest = this.form.getRawValue();
    this.auth.login(payload).subscribe({
      next: (res) => {
        this.auth.storeAuth(res);            
        this.loading = false;
        this.router.navigateByUrl('/movies');       
      },
      error: (e) => {
        this.loading = false;
        this.serverErr = typeof e?.error === 'string' ? e.error : 'Neuspešna prijava.';
      }
    });
  }

  ngOnDestroy() { this.qpSub?.unsubscribe(); }
}
