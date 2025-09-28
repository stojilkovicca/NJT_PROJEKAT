import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';
import { RegisterComponent } from './pages/register/register';
import { LoginComponent } from './pages/login/login';  

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },      
  { path: '**', redirectTo: '' }
];
