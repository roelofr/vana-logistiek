import {CanActivate, CanActivateChild, GuardResult, MaybeAsync, Router} from '@angular/router';
import {inject, Injectable} from '@angular/core';
import {AuthService} from '../../services/global/auth.service';

@Injectable({
    providedIn: 'root'
})
export class NotLoggedInGuard implements CanActivate, CanActivateChild {
    private readonly router = inject(Router);
    private readonly authService = inject(AuthService);


    checkIsGuest(): boolean {
        if (!this.authService.isLoggedIn()) {
            return true;
        }

        this.router.navigate(['/']);

        return false;
    }

    canActivate(): MaybeAsync<GuardResult> {
        return this.checkIsGuest();
    }

    canActivateChild(): MaybeAsync<GuardResult> {
        return this.canActivate();
    }
}
