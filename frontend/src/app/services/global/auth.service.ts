import {computed, Injectable, Signal, signal} from '@angular/core';
import {PersistenceService} from '../persistence/persistence.service';
import {jwtDecode, JwtPayload} from "jwt-decode";
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {catchError, Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';


const JWT_KEY = 'user-jwt';
const RETURN_URL = 'return-url';

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
  public readonly username: Signal<string | null> = computed(() => this.userJwt()?.sub ?? null);
  public readonly isLoggedIn: Signal<boolean> = computed(() => this.userJwt() != null);
  public readonly logoutReason = signal<string>('');

  private readonly nextUrl = signal<string|null>(null);
  private readonly userJwt = signal<null | JwtPayload>(null);

  constructor(
    private readonly persistenceService: PersistenceService,
    private readonly http: HttpClient,
    private readonly router: Router
  ) {
    this.loadTokenFromStorage();
  }

  private loadTokenFromStorage() {
    const userToken = this.persistenceService.get(JWT_KEY);

    if (!userToken)
      return;

    this.userJwt.set(parseNonExpiredJwt(userToken));
  }

  public setReturnUrl(url: string) {
    this.nextUrl.set(url);
  }

  public getReturnUrl() {
    return this.nextUrl();
  }

  /**
   * Authenticates the user with the given username and password. Returns an error
   * message on failure, or nothing on success.
   * @param username
   * @param password
   */
  public authenticate(username: string, password: string): Observable<AuthResponse> {
    const data = new FormData();
    data.append('username', username);
    data.append('password', password);

    return this.http.post<UserJwt>('/api/auth', data)
      .pipe(
        map(this.authResponse.bind(this)),
        catchError(this.authError.bind(this))
      );
  }

  public logout(reason: string = 'logout'): void {
    this.persistenceService.forget(JWT_KEY);
    this.userJwt.set(null);

    this.logoutReason.set(reason);

    this.router.navigate(['/']);
  }

  private authResponse({token}: UserJwt) {
    const parsedToken = parseNonExpiredJwt(token);
    if (!parsedToken)
      throw new Error('Failed to parse user JWT');

    this.userJwt.set(parsedToken);
    this.persistenceService.store(JWT_KEY, token)

    console.log('Logged in as %s', parsedToken.sub);

    return AuthResponse.ok();
  }

  private authError(err: HttpErrorResponse): Observable<AuthResponse> {
    const make = (error: string) => of(AuthResponse.error(error));

    switch (err.status) {
      case 400:
      case 401:
        return make(`Invalid credentials.`);

      case 403:
        return make(`This account is not active.`);

      case 419:
        return make(`Too many login attempts.`);

      case 503:
        return make(`Service disruption.`);

      case 500:
      case 501:
      case 502:
        return make(`Server error.`);

      default:
        return make(`Unknown error occurred.`);
    }
  }
}

class AuthResponse {
  static ok() {
    return new AuthResponse(true, null);
  }

  static error(error: string) {
    return new AuthResponse(false, error);
  }

  private constructor(
    public readonly ok: boolean,
    public readonly error: string | null) {
    //
  }
}

interface UserJwt {
  token: string;
}
