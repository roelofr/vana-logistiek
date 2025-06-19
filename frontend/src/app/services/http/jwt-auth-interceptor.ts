import {HttpInterceptorFn} from '@angular/common/http';
import {AuthService} from '../global/auth.service';
import {inject} from '@angular/core';

export const jwtAuthInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  if (!authService.isLoggedIn())
    return next(req);

  const newReq = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${authService.jwt}`),
  })

  return next(newReq);
};
