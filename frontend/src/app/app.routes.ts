import { Routes } from '@angular/router';

export const routes: Routes = [
  // Guest routes
  {
    path: 'login',
    title: 'Inloggen',
    loadComponent: async () =>
      (await import('./views/login/login.component')).LoginComponent,
    data: {
      headless: true,
    },
  },

  // User routes
  {
    path: 'dashboard',
    title: 'Dashboard',
    loadComponent: async () =>
      (await import('./views/homepage/homepage.component')).HomepageComponent,
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
    redirectTo: 'dashboard',
  },
];
