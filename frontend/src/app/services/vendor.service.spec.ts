import {TestBed} from '@angular/core/testing';

import {VendorService} from './vendor.service';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('VendorService', () => {
  let service: VendorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideHttpClient(withFetch()),
        provideHttpClientTesting(),
      ]
    });
    service = TestBed.inject(VendorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
