import {Routes} from '@angular/router';
import {UserShellComponent} from './shared/user-shell/user-shell.component';

export const routes: Routes = [
  // Guest routes
  {
    path: 'login',
    title: 'Inloggen',
    loadComponent: async () =>
      (await import('./views/login/login.component')).LoginComponent,
  },
  {
    path: 'register',
    title: 'Registreren',
    loadComponent: async () =>
      (await import('./views/register/register.component')).RegisterComponent,
  },

  // User routes
  {
    path: '',
    component: UserShellComponent,
    children: [
      {
        path: 'dashboard',
        title: 'Dashboard',
        loadComponent: async () =>
          (await import('./views/homepage/homepage.component')).HomepageComponent,
      }
    ]
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
