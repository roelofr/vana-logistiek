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
}

interface AuthResponse {
  name: string,
  jwt: string,
  expiration: string,
}

export class AuthError extends Error {
}
