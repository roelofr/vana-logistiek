import { CanActivateFn } from '@angular/router';

export const loggedInGuardGuard: CanActivateFn = (route, state) => {
  return true;
};
