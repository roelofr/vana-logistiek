import {TestBed} from '@angular/core/testing';

import {LoggedInGuard} from './logged-in.guard';
import {AuthService} from '../../services/global/auth.service';
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from "@angular/router";
import {provideExperimentalZonelessChangeDetection, signal} from "@angular/core";

class DummyAuthService {
    public readonly isLoggedIn = signal(false);
    private returnUrl: string | null = null;

    setReturnUrl(url: string) {
        this.returnUrl = url;
    }

    getReturnUrl() {
        return this.returnUrl;
    }
}

describe('LoggedInGuard', () => {
    let guard: LoggedInGuard;
    let authService: DummyAuthService;
    let lastDestination: string[];

    const activatedRoute = {} as ActivatedRouteSnapshot;
    const routerState = {url: '/login'} as RouterStateSnapshot
    const router = {
        navigate(destination: string[]) {
            lastDestination = destination;
        }
    } as Router

    beforeEach(async () => {
        lastDestination = [];
        authService = new DummyAuthService();

        TestBed.configureTestingModule({
            providers: [
                provideExperimentalZonelessChangeDetection(),
                {provide: Router, useValue: router},
                {provide: AuthService, useValue: authService}
            ]
        });

        guard = TestBed.inject(LoggedInGuard);
    });

    it('should be created', () => {
        expect(guard).toBeTruthy();
    });

    it('should work when logged in', async () => {
        authService.isLoggedIn.set(true);

        const result = guard.canActivate(activatedRoute, routerState)
        expect(result).toEqual(true);

        const resultChild = guard.canActivateChild(activatedRoute, routerState)
        expect(resultChild).toEqual(true);
    });

    it('should return a redirect when not logged in', async () => {
        authService.isLoggedIn.set(false);

        expect(lastDestination).toEqual([]);

        const result = guard.canActivate(activatedRoute, routerState)
        expect(result).toEqual(false);

        expect(lastDestination).toEqual(['/login']);

        const resultChild = guard.canActivateChild(activatedRoute, routerState)
        expect(resultChild).toEqual(false);
    });
});
