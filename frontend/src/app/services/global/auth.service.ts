import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {shareReplay} from 'rxjs/operators';
import {DateTime} from 'luxon';
import {lastValueFrom} from 'rxjs';
import {jwtDecode} from 'jwt-decode';

const AUTH_NAME = 'auth.sub';
const AUTH_JWT = 'auth.jwt';
const AUTH_EXPIRATION = 'auth.exp';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);

  get name(): string | null {
    const value = localStorage.getItem(AUTH_NAME);
    return (value) ? value : null;
  }

  get jwt(): string | null {
    const value = localStorage.getItem(AUTH_JWT)
    return (value) ? value : null;
  }

  get expiration(): DateTime | null {
    const expiration = localStorage.getItem(AUTH_EXPIRATION);
    return expiration ? DateTime.fromISO(expiration as string) : null;
  }

  async login(username: string, password: string) {
    const subscribable = this.http.post<AuthResponse>('/api/auth/login', {username, password})
      .pipe(shareReplay());

    try {
      const response = await lastValueFrom(subscribable);
      this.handleResponse(response);
    } catch (error) {
      if (error instanceof HttpErrorResponse)
        return this.handleErrorResponse(error);

      console.error('Unknown login error %o', error);
      throw new AuthError(`Onbekende fout.`, {cause: error});
    }
  }

  logout() {
    localStorage.removeItem(AUTH_NAME);
    localStorage.removeItem(AUTH_JWT);
    localStorage.removeItem(AUTH_EXPIRATION);
  }

  public isLoggedIn() {
    return this.expiration && this.expiration > DateTime.now();
  }

  private handleErrorResponse(err: HttpErrorResponse): void {
    switch (err.status) {
      case 400:
      case 401:
        throw new AuthError(`Onjuiste inloggegevens.`);

      case 403:
        throw new AuthError(`Deze account is niet geactiveerd.`);

      case 419:
        throw new AuthError(`Te veel inlogpogingen.`);

      case 503:
        throw new AuthError(`Er is even iets down.`);

      case 500:
      case 501:
      case 502:
        throw new AuthError(`Fout op de server.`, {cause: err});

      default:
        throw new AuthError(`Onbekende fout.`, {cause: err});
    }
  }

  private handleResponse(result: AuthResponse): void {
    const expiresAt = DateTime.fromISO(result.expiration);

    localStorage.setItem(AUTH_NAME, result.name);
    localStorage.setItem(AUTH_JWT, result.jwt);
    localStorage.setItem(AUTH_EXPIRATION, expiresAt.toISO() as string);

    console.log("Logged in as %o, token valid thru %s", result.name, expiresAt.toLocaleString({
      dateStyle: "short",
      timeStyle: "short"
    }))
  }

  get roles(): string[] | null {
    const token = this.jwt;
    if (!token)
      return null;

    const audience = jwtDecode(token).aud ?? [];
    return typeof audience == 'string' ? [audience] : audience;
  }
}

interface AuthResponse {
  name: string,
  jwt: string,
  expiration: string,
}

export class AuthError extends Error {
}
