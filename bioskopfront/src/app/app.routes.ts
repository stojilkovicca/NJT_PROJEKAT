import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';
import { RegisterComponent } from './pages/register/register';
 

 
import { adminGuard } from './core/admin.guard';
import { MoviesComponent } from './pages/movies/movies';
import { MovieDetailsComponent } from './pages/movie-details/movie-details';
import { AdminLayoutComponent } from './admin/admin-layout/admin-layout';
import { MoviesPageComponent } from './admin/movies-page/movies-page';
import { MovieFormComponent } from './admin/movie-form/movie-form';
import { ProjectionsPageComponent } from './admin/projections-page/projections-page';
import { HallsPageComponent } from './admin/halls-page/halls-page';
import { LoginComponent } from './pages/login/login';
import { ReservationsPageComponent } from './admin/reservations-page/reservations-page';
import VerifyComponent from './pages/verify/verify';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'register', component: RegisterComponent }, 
   { path: 'verify', component: VerifyComponent },    
   { path: 'login', component: LoginComponent },
  { path: 'movies', component: MoviesComponent },
  { path: 'movies/:id', component: MovieDetailsComponent },

  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],
    children: [
      { path: '', redirectTo: 'movies', pathMatch: 'full' },
      { path: 'movies', component: MoviesPageComponent },
      { path: 'movies/new', component: MovieFormComponent },
      { path: 'projections', component: ProjectionsPageComponent },
      { path: 'halls', component: HallsPageComponent },
        { path: 'reservations', component: ReservationsPageComponent }
    ]
  },

  { path: '**', redirectTo: '' }
];
