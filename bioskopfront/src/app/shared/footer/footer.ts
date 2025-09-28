import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
 
  template: `
    <div class="foot">
      <div class="container">
        <p>© {{ year }} Bioskop • Sva prava zadržana</p>
        <nav class="links">
          <a href="#">Politika privatnosti</a>
          <a href="#">Uslovi korišćenja</a>
          <a href="#">Kontakt</a>
        </nav>
      </div>
    </div>
  `,
 
  styles: [`
    .foot {
      border-top: 1px solid rgba(255,255,255,.08);
      padding: 20px 0;
      color: #98a1b3;
      background: linear-gradient(180deg, rgba(255,255,255,0.02), rgba(255,255,255,0));
    }
    .container {
      max-width: 1100px;
      margin: 0 auto;
      padding: 0 16px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
    }
    .links { display: flex; gap: 14px; }
    .links a {
      color: #cfd3dc; text-decoration: none; font-size: .95rem;
    }
    .links a:hover { color: #fff; }
  `]
})
export class FooterComponent {
  year = new Date().getFullYear();
}
