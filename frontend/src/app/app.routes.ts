import {Routes} from '@angular/router'
import {LoginComponent} from './views/login/login.component';
import {HomepageComponent} from './views/homepage/homepage.component';

export const routes: Routes = [
  // Guest routes
  {
    path: 'login',
    title: 'Inloggen',
    component: LoginComponent,
    data: {
      noUi: true,
    }
  },

  // User routes
  {
    path: 'dashboard',
    title: 'Dashboard',
    component: HomepageComponent,
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
