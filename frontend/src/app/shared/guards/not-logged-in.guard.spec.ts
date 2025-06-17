import {TestBed} from '@angular/core/testing';

import {NotLoggedInGuard} from './not-logged-in.guard';
import {AuthService} from '../../services/global/auth.service';
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from "@angular/router";
import {provideExperimentalZonelessChangeDetection} from "@angular/core";

describe('NotLoggedInGuard', () => {
  let guard: NotLoggedInGuard;
  const authServiceSpy = Jest.mock('AuthService', ['isLoggedIn']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  const activatedRoute = {} as ActivatedRouteSnapshot;
  const routerState = {url: '/login'} as RouterStateSnapshot

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
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
    authServiceSpy.isLoggedIn.and.returnValue(true);

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

  it('should no-op when not logged in', async () => {
    authServiceSpy.isLoggedIn.and.returnValue(false);

    const result = guard.canActivate(activatedRoute, routerState)
    expect(result).toEqual(true);

    expect(authServiceSpy.isLoggedIn.calls.count())
      .toBe(1);

    expect(routerSpy.navigate.calls.count())
      .toBe(0);

    const resultChild = guard.canActivateChild(activatedRoute, routerState)
    expect(resultChild).toEqual(true);
  });
});
