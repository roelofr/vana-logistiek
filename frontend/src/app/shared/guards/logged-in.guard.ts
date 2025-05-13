import {Injectable} from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivate,
    CanActivateChild,
    GuardResult,
    MaybeAsync,
    Router,
    RouterStateSnapshot
} from '@angular/router';
import {AuthService} from '../../services/global/auth.service';

@Injectable({
    providedIn: 'root'
})
export class LoggedInGuard implements CanActivate, CanActivateChild {
    constructor(
        private readonly router: Router,
        private readonly authService: AuthService) {
        //
    }

    checkLogin(url: string): boolean {
        if (this.authService.isLoggedIn()) {
            return true;
        }

        this.authService.setReturnUrl(url);

        this.router.navigate(['/login']);

        return false;
    }

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): MaybeAsync<GuardResult> {
        return this.checkLogin(state.url);
    }

    canActivateChild(
        childRoute: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): MaybeAsync<GuardResult> {
        return this.canActivate(childRoute, state);
    }
}
