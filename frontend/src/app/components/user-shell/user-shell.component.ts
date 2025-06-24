import {AfterViewInit, Component, inject, viewChild} from '@angular/core';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {AppShellComponent} from '../../global/app-shell/app-shell.component';

@Component({
  selector: 'app-user-shell',
  imports: [
    AppShellComponent,
    RouterOutlet,

  ],
  templateUrl: './user-shell.component.html'
})
export class UserShellComponent implements AfterViewInit {
  readonly router = inject(Router);
  readonly activatedRoute = inject(ActivatedRoute);

  readonly outlet = viewChild.required<RouterOutlet>('outlet');

  /**
   * clean shit up if we're on the wrong page again.
   */
  ngAfterViewInit() {
    if (!this.outlet().isActivated)
      this.router.navigate(['/home']);
  }
}
