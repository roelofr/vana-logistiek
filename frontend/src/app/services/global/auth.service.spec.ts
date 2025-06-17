import {TestBed} from '@angular/core/testing';

import {AuthService} from './auth.service';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideHttpClient(withFetch()),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
