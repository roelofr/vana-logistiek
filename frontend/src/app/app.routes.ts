import {Routes} from '@angular/router';
import {UserShellComponent} from './shared/user-shell/user-shell.component';
import {LoggedInGuard} from './shared/guards/logged-in.guard';

export const routes: Routes = [
  // Redirects
  {
    path: '',
    redirectTo: 'dashoard',
    pathMatch: 'full',
  },

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
    canActivate: [LoggedInGuard],
    children: [
      {
        path: 'dashboard',
        title: 'Dashboard',
        loadComponent: async () =>
          (await import('./views/homepage/homepage.component')).HomepageComponent,
      },
      {
        path: 'vent',
        title: 'Klaag-i-nator',
        loadComponent: async () =>
          (await import('./views/venting/venting.component')).VentingComponent
      }
    ]
  },

  // Fallback
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];
