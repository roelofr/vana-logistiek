import {Routes} from '@angular/router';
import {UserShellComponent} from './components/user-shell/user-shell.component';
import {LoggedInGuard} from './components/guards/logged-in.guard';
import {NotLoggedInGuard} from './components/guards/not-logged-in.guard';

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
    canActivate: [NotLoggedInGuard],
    loadComponent: async () =>
      (await import('./views/login/login.component')).LoginComponent,
  },
  {
    path: 'register',
    title: 'Registreren',
    canActivate: [NotLoggedInGuard],
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
      },
      {
        path: 'tickets/nieuw',
        title: 'Nieuw ticket',
        loadComponent: async () =>
          (await import('./views/create-ticket/create-ticket.component')).CreateTicketComponent
      }
    ]
  },

  // Fallback
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];
