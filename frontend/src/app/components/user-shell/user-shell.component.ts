import {AfterViewChecked, Component, inject, signal} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {AppShellComponent} from '../../global/app-shell/app-shell.component';

@Component({
  selector: 'app-user-shell',
  imports: [
    AppShellComponent,
    RouterOutlet,

  ],
  templateUrl: './user-shell.component.html'
})
export class UserShellComponent implements AfterViewChecked {
  readonly router = inject(Router);
  readonly isAttached = signal(false);

  /**
   * clean shit up if we're on the wrong page again.
   */
  ngAfterViewChecked(): void {
    if (!this.isAttached())
      this.router.navigate(["/home"])
  }
}
