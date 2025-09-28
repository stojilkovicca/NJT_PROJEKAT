import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  Validators,
  FormGroup,
  FormControl
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="auth">
      <div class="card">
        <h1>Kreiraj nalog</h1>
        <p class="muted">Registruj se i rezerviši karte online.</p>

        <form [formGroup]="form" (ngSubmit)="submit()">
          <label>
            Korisničko ime
            <input type="text" formControlName="username" placeholder="npr. milica" />
          </label>
          <div class="err" *ngIf="f.username.touched && f.username.invalid">
            {{ f.username.errors?.['required'] ? 'Korisničko ime je obavezno.' : '' }}
            {{ f.username.errors?.['minlength'] ? 'Min 3 karaktera.' : '' }}
          </div>

          <label>
            Email
            <input type="email" formControlName="email" placeholder="npr. milica@mail.com" />
          </label>
          <div class="err" *ngIf="f.email.touched && f.email.invalid">
            {{ f.email.errors?.['required'] ? 'Email je obavezan.' : '' }}
            {{ f.email.errors?.['email'] ? 'Unesite validan email.' : '' }}
          </div>

          <label>
            Lozinka
            <input type="password" formControlName="password" placeholder="••••••••" />
          </label>
          <div class="err" *ngIf="f.password.touched && f.password.invalid">
            {{ f.password.errors?.['required'] ? 'Lozinka je obavezna.' : '' }}
            {{ f.password.errors?.['minlength'] ? 'Min 6 karaktera.' : '' }}
          </div>

          <button class="btn primary" type="submit" [disabled]="form.invalid || loading">
            {{ loading ? 'Slanje...' : 'Registracija' }}
          </button>

          <p class="small">
            Već imaš nalog?
            <a routerLink="/login">Prijava</a>
          </p>

          <p class="err" *ngIf="serverErr">{{ serverErr }}</p>
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
export class RegisterComponent {
  loading = false;
  serverErr = '';

  // ✅ strogo tipizirana forma (nema TS4111)
  form!: FormGroup<{
    username: FormControl<string>;
    email:    FormControl<string>;
    password: FormControl<string>;
  }>;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      username: this.fb.nonNullable.control('', [Validators.required, Validators.minLength(3)]),
      email:    this.fb.nonNullable.control('', [Validators.required, Validators.email]),
      password: this.fb.nonNullable.control('', [Validators.required, Validators.minLength(6)]),
    });
  }

  get f() { return this.form.controls; }

  submit() {
    this.serverErr = '';
    if (this.form.invalid) return;

    this.loading = true;
    this.auth.register(this.form.getRawValue()).subscribe({
      next: () => {
        this.loading = false;
        //   posle registracije: poruka + redirect na /login
        this.router.navigate(['/login'], { queryParams: { registered: '1' } });
      },
      error: (e) => {
        this.loading = false;
        this.serverErr = typeof e?.error === 'string' ? e.error : 'Registracija nije uspela.';
      }
    });
  }

}
