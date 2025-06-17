import {inject, Injectable} from '@angular/core';
import {CanActivate, CanActivateChild, GuardResult, MaybeAsync, Router} from '@angular/router';
import {AuthService} from '../../services/global/auth.service';

@Injectable({
  providedIn: 'root'
})
export class LoggedInGuard implements CanActivate, CanActivateChild {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  checkIsLoggedIn(): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    this.router.navigate(['/login']);

    return false;
  }

  canActivate(): MaybeAsync<GuardResult> {
    return this.checkIsLoggedIn();
  }

  canActivateChild(): MaybeAsync<GuardResult> {
    return this.canActivate();
  }
}
