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
        path: 'home',
        title: 'Homepage',
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
        path: 'tickets',
        title: 'Ticketoverzicht',
        loadComponent: async () =>
          (await import('./views/ticket/index/index.component')).IndexComponent
      },
      {
        path: 'tickets/nieuw',
        title: 'Nieuw ticket',
        loadComponent: async () =>
          (await import('./views/ticket/create/create.component')).CreateComponent
      },
      {
        path: 'tickets/:ticketId',
        title: 'Ticketoverzicht',
        loadComponent: async () =>
          (await import('./views/ticket/show/show.component')).ShowComponent
      },
    ]
  },

  // Fallback
  {
    path: '**',
    redirectTo: 'home',
  },
];
