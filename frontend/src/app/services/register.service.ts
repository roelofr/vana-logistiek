import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private readonly http: HttpClient) {
  }

  /**
   * Registers a user. The user must have accepted the terms before registering.
   * @param name
   * @param email
   * @param password
   */
  async register(
    name: string,
    email: string,
    password: string
  ): Promise<void> {
    const body = {name, email, password, acceptTerms: true};
    const authRegister = this.http.post(`/api/auth/register`, body, {observe: 'response'});

    try {
      const registerResult = await lastValueFrom(authRegister);
      if (registerResult.status !== 201)
        throw new Error('Registration status failed with non-error code');
    } catch (e) {
      if (e instanceof HttpErrorResponse) {
        if (e.status === 400) // Bad Request
          throw new RegisterError('Missing required fields.', RegisterErrorCode.BadRequest);
        if (e.status === 409) // Conflict
          throw new RegisterError('Account already exists.', RegisterErrorCode.AccountExists);
      }

      console.error('Unknwon error %o while registering', e);
      throw new RegisterError('Unknown error', RegisterErrorCode.Unknown);
    }
  }
}

export class RegisterError extends Error {
  readonly code: RegisterErrorCode;

  constructor(message: string, code: RegisterErrorCode) {
    super(message);
    this.code = code;
  }
}

enum RegisterErrorCode {
  BadRequest,
  AccountExists,
  Unknown,
}
