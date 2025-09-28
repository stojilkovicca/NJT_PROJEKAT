import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';
import { RegisterComponent } from './pages/register/register';
import { LoginComponent } from './pages/login/login';
 
 
import { MoviesComponent } from './pages/movies/movies';
import { MovieDetailsComponent } from './pages/movie-details/movie-details';
import { authGuard } from './core/auth-guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },

  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },

  { path: 'movies', component: MoviesComponent, canActivate: [authGuard] },
  { path: 'movies/:id', component: MovieDetailsComponent, canActivate: [authGuard] },

  { path: '**', redirectTo: '' }
];
