import {TestBed} from '@angular/core/testing';

import {VentingService} from './venting.service';
import {provideExperimentalZonelessChangeDetection} from '@angular/core';

describe('VentingService', () => {
  let service: VentingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
      ],
    });
    service = TestBed.inject(VentingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
