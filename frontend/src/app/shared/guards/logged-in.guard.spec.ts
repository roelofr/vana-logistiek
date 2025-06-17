import {TestBed} from '@angular/core/testing';

import {LoggedInGuard} from './logged-in.guard';
import {AuthService} from '../../services/global/auth.service';
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from "@angular/router";
import {provideZonelessChangeDetection} from "@angular/core";

describe('LoggedInGuard', () => {
  let guard: LoggedInGuard;

  const authServiceSpy = jasmine.createSpyObj('AuthService', ['isLoggedIn']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  const activatedRoute = {} as ActivatedRouteSnapshot;
  const routerState = {url: '/login'} as RouterStateSnapshot

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        LoggedInGuard,
        {provide: Router, useValue: authServiceSpy},
        {provide: AuthService, useValue: routerSpy}
      ]
    });

    guard = TestBed.inject(LoggedInGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should work when logged in', async () => {
    authServiceSpy.isLoggedIn.and.returnValue(true);

    const result = guard.canActivate(activatedRoute, routerState)
    expect(result).toEqual(true);

    expect(authServiceSpy.isLoggedIn.calls.count())
      .toBe(1);

    expect(routerSpy.navigate.calls.count())
      .toBe(0);

    const resultChild = guard.canActivateChild(activatedRoute, routerState)
    expect(resultChild).toEqual(true);
  });

  it('should return a redirect when not logged in', async () => {
    authServiceSpy.isLoggedIn.and.returnValue(false);

    const result = guard.canActivate(activatedRoute, routerState)
    expect(result).toEqual(false);

    expect(authServiceSpy.isLoggedIn.calls.count())
      .toBe(1);

    expect(routerSpy.navigate.calls.count())
      .toBe(1);

    expect(routerSpy.navigate.calls[0].args)
      .toBe(['/login']);

    const resultChild = guard.canActivateChild(activatedRoute, routerState)
    expect(resultChild).toEqual(false);
  });
});
