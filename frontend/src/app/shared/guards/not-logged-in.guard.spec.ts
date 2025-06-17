import {TestBed} from '@angular/core/testing';

import {NotLoggedInGuard} from './not-logged-in.guard';
import {AuthService} from '../../services/global/auth.service';
import {Router} from "@angular/router";
import {provideZonelessChangeDetection, signal} from "@angular/core";

describe('NotLoggedInGuard', () => {
    let guard: NotLoggedInGuard;
    const authServiceSpy = {
        isLoggedIn: jest.fn(signal(false)),
    };
    const routerSpy = {
        navigate: jest.fn(() => Promise.resolve()),
    };

    beforeEach(async () => {
        TestBed.configureTestingModule({
            providers: [
                provideZonelessChangeDetection(),
                NotLoggedInGuard,
                {provide: Router, useValue: authServiceSpy},
                {provide: AuthService, useValue: routerSpy}
            ]
        });

        guard = TestBed.inject(NotLoggedInGuard);
    });

    it('should be created', () => {
        expect(guard).toBeTruthy();
    });

    it('should return a redirect when logged in', async () => {
        authServiceSpy.isLoggedIn.mockReturnValue(true);

        const result = guard.canActivate()
        expect(result).toEqual(false);

        expect(authServiceSpy.isLoggedIn.mock.calls.length)
            .toBe(1);

        expect(routerSpy.navigate.mock.calls.length)
            .toBe(1);

        expect(routerSpy.navigate.mock.calls[0])
            .toBe(['/login']);

        const resultChild = guard.canActivateChild()
        expect(resultChild).toEqual(false);
    });

    it('should no-op when not logged in', async () => {
        authServiceSpy.isLoggedIn.mockReturnValue(false);

        const result = guard.canActivate()
        expect(result).toEqual(true);

        expect(authServiceSpy.isLoggedIn.mock.calls.length)
            .toBe(1);

        expect(routerSpy.navigate.mock.calls.length)
            .toBe(0);

        const resultChild = guard.canActivateChild()
        expect(resultChild).toEqual(true);
    });
});
