import {Routes} from '@angular/router'
import {LoginComponent} from './views/login/login.component';

export const routes: Routes = [
  // Guest routes
  {
    path: 'login',
    title: 'Inloggen',
    loadComponent: () => import('./views/login/login.component').then(c => c.LoginComponent),
    data: {
      headless: true,
    }
  },

  // User routes
  {
    path: 'dashboard',
    title: 'Dashboard',
    loadComponent: () => import('./views/homepage/homepage.component').then(c => c.HomepageComponent),
  },

  // Redirects
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },

  // Fallback
  {
    path: '**',
    redirectTo: 'dashboard'
  },
]
