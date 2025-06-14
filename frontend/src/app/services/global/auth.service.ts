import {Injectable} from '@angular/core';
import {jwtDecode, JwtPayload} from "jwt-decode";
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {shareReplay} from 'rxjs/operators';
import {DateTime} from 'luxon';
import {lastValueFrom} from 'rxjs';

const AUTH_NAME = 'auth.sub';
const AUTH_JWT = 'auth.jwt';
const AUTH_EXPIRATION = 'auth.exp';

const parseNonExpiredJwt = (token: string) => {
  try {
    const jwt = jwtDecode<JwtPayload>(token);
    return jwt.exp && jwt.exp > Date.now() / 1000 ? jwt : null;
  } catch (e) {
    console.error('Failed to parse JWT: %o', e);
    return null;
  }
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {

  }

  async login(email: string, password: string) {
    const subscribable = this.http.post<AuthResponse>('/api/auth/login', {email, password})
        .pipe(shareReplay());

    try {
      const response = await lastValueFrom(subscribable);
      console.log('Login resp = %o', response);
      this.handleResponse(response);
    } catch (e: HttpErrorResponse) {
      console.error('Login error = %o', e);
      this.handleErrorResponse(e);
    }
  }

  logout() {
    localStorage.removeItem(AUTH_NAME);
    localStorage.removeItem(AUTH_JWT);
    localStorage.removeItem(AUTH_EXPIRATION);
  }


  get name(): string | null {
    const value = localStorage.getItem(AUTH_NAME);
    return (value) ? value : null;
  }

  get jwt(): string | null {
    const value = localStorage.getItem(AUTH_JWT)
    return (value) ? value : null;
  }

  get expiration(): DateTime {
    const expiration = localStorage.getItem(AUTH_EXPIRATION);
    return expiration ? DateTime.fromISO(expiration) : null;
  }

  public isLoggedIn() {
    return this.expiration < DateTime.now();
  }

  private handleErrorResponse(err: HttpErrorResponse): void {
    switch (err.status) {
      case 400:
      case 401:
        throw new AuthError((`Invalid credentials.`);

      case 403:
        throw new AuthError(`This account is not active.`);

      case 419:
        throw new AuthError(`Too many login attempts.`);

      case 503:
        throw new AuthError(`Service disruption.`);

      case 500:
      case 501:
      case 502:
        throw new AuthError(`Server error.`);

      default:
        throw new AuthError(`Unknown error occurred.`, {cause: err});
    }

  private handleResponse(result: AuthResponse): void {
      const expiresAt = new DateTime(result.expiration);

      localStorage.setItem(AUTH_NAME, result.name);
      localStorage.setItem(AUTH_JWT, result.jwt);
      localStorage.setItem(AUTH_EXPIRATION, expiresAt.toISO());
    }
  }
}

interface AuthResponse {
  name: string,
  jwt: string,
  expiration: Date,
}

class AuthError extends Error {
}
