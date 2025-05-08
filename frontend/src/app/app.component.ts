import { Component, signal } from '@angular/core';
import {
  NavigationEnd,
  Route,
  RouteConfigLoadEnd,
  Router,
  RouterOutlet,
} from '@angular/router';
import { AppShellComponent } from './global/app-shell/app-shell.component';
import { filter } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AppShellComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'Frontend';

  readonly hideUi = signal(false);
  readonly nextRoute = signal<null | Route>(null);

  constructor(router: Router) {
    this.bindRouterEvents(router);
  }

  private bindRouterEvents(router: Router) {
    router.events
      .pipe(
        filter((event) => event instanceof RouteConfigLoadEnd),
        map((event) => event.route),
      )
      .subscribe((route) => this.nextRoute.set(route));

    router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => this.nextRoute() as Route),
      )
      .subscribe((route) =>
        this.hideUi.set((route.data && route.data['headless']) === true),
      );
  }
}
