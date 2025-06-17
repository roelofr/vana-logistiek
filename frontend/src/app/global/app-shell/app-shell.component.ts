import {Component, computed, inject, signal} from '@angular/core';
import {MatSidenavModule} from '@angular/material/sidenav';
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
  styleUrl: './app-shell.component.scss',
})
export class AppShellComponent {
  isDesktop = signal(true);
  private readonly confettiService = inject(ConfettiService);
  private readonly authService = inject(AuthService);
  username = computed(() => this.authService.name)

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

  fireConfetti(): void {
    this.confettiService.dispenseConfetti('strong');
  }
}
