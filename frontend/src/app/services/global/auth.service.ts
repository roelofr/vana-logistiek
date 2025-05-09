import {Injectable, signal} from '@angular/core';
import {PersistenceService} from '../persistence/persistence.service';
import {jwtDecode, JwtPayload} from "jwt-decode";


const JWT_KEY = 'user-jwt';
const JWT_REFRESH_KEY = 'user-jwt-refresh';

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
  private readonly userJwt = signal<null | JwtPayload>();
  private readonly refreshJwt = signal<null | JwtPayload>();
  public readonly isLoggedIn = signal(false);

  constructor(
    private readonly persistenceService: PersistenceService,
  ) {
    this.reload();
  }

  private reload() {
    const userToken = this.persistenceService.get(JWT_KEY);
    const refreshToken = this.persistenceService.get(JWT_REFRESH_KEY);

    if (!userToken || !refreshToken)
      return;

    const userJwt = parseNonExpiredJwt(userToken);
    const refreshJwt = parseNonExpiredJwt(refreshToken);

    if (userJwt) {
      this.isLoggedIn.set(true);
      this.username.set(userJwt
    }

    if (!refreshJwt)
      return;

  }
}

class Jwt {
  public readonly expiresAt: Date;

  constructor(public readonly jwt: JwtPayload) {
    this.expiresAt = new Date(jwt.exp * 1000);
    this.username = jwt.sub;
    this.roles = jwt.aud;
  }
}
