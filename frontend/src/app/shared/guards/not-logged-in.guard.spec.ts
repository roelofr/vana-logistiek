import {TestBed} from '@angular/core/testing';

import {NotLoggedInGuard} from './not-logged-in.guard';
import {AuthService} from '../../services/global/auth.service';
import {Router} from "@angular/router";
import {provideZonelessChangeDetection, signal} from "@angular/core";

// Mocks
const mockIsLoggedIn = jest.fn(signal(false));
const mockNavigate = jest.fn(() => Promise.resolve());

class MockAuthService {
  isLoggedIn() {
    return mockIsLoggedIn();
  };
}

class MockRouter {
  navigate(route: Array<unknown>) {
    return mockNavigate.call(undefined, route);
  }
}

describe('NotLoggedInGuard', () => {
  let guard: NotLoggedInGuard;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        NotLoggedInGuard,
        {provide: Router, useValue: new MockRouter},
        {provide: AuthService, useValue: new MockAuthService},
      ]
    });

    guard = TestBed.inject(NotLoggedInGuard);
  });

  afterEach(() => {
    mockIsLoggedIn.mockClear();
    mockNavigate.mockClear();
  })

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return a redirect when logged in', async () => {
    mockIsLoggedIn.mockReturnValue(true);

    const result = guard.canActivate()
    expect(result).toEqual(false);

    expect(mockIsLoggedIn.mock.calls.length)
      .toBe(1);

    expect(mockNavigate.mock.calls.length)
      .toBe(1);

    expect(mockNavigate.mock.calls[0])
      .toStrictEqual([['/']]);

    const resultChild = guard.canActivateChild()
    expect(resultChild).toEqual(false);
  });

  it('should no-op when not logged in', async () => {
    mockIsLoggedIn.mockReturnValue(false);

    const result = guard.canActivate()
    expect(result).toEqual(true);

    expect(mockIsLoggedIn.mock.calls.length)
      .toBe(1);

    expect(mockNavigate.mock.calls.length)
      .toBe(0);

    const resultChild = guard.canActivateChild()
    expect(resultChild).toEqual(true);
  });
});
