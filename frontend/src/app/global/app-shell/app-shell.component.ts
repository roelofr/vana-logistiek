import {Component, computed, inject, signal, viewChild} from '@angular/core';
import {MatSidenav, MatSidenavModule} from '@angular/material/sidenav';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {MatDividerModule} from '@angular/material/divider';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';

import {map, shareReplay} from 'rxjs/operators';
import {ConfettiService} from '../confetti/confetti.service';
import {AppNavComponent} from '../app-nav/app-nav.component';
import {AuthService} from '../../services/global/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-shell',
  imports: [
    AppNavComponent,
    MatSidenavModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule
  ],
  templateUrl: './app-shell.component.html',
  styleUrl: './app-shell.component.css',
})
export class AppShellComponent {
  private readonly confettiService = inject(ConfettiService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly isDesktop = signal(true);
  readonly username = computed(() => this.authService.name)
  readonly sidebar = viewChild.required<MatSidenav>('sidebar');

  constructor() {
    const breakpointObserver = inject(BreakpointObserver);

    breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small])
      .pipe(
        map((result) => !result.matches),
        shareReplay(),
      )
      .subscribe((matches) => this.isDesktop.set(matches));
  }

  /**
   * Poof!
   */
  fireConfetti(): void {
    this.confettiService.dispenseConfetti('strong');
  }

  /**
   * Allow the user the dignity to log out.
   */
  doLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  /**
   * Close the sidebar on mobile, if it's open.
   */
  maybeCloseSidebar() {
    if (this.isDesktop())
      return;

    if (this.sidebar().opened)
      this.sidebar().close();
  }
}
