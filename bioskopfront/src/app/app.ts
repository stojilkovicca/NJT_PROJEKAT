import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/navbar/navbar';
import { FooterComponent } from './shared/footer/footer';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, FooterComponent],
  template: `
    <app-navbar></app-navbar>

    <!-- ovde Angular ubacuje svaku rutu / stranicu -->
    <router-outlet></router-outlet>

    <app-footer></app-footer>
  `,
})
export class AppComponent {}
