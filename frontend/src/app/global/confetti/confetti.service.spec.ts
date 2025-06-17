import {TestBed} from '@angular/core/testing';

import {ConfettiService} from './confetti.service';
import {provideZonelessChangeDetection} from '@angular/core';

describe('ConfettiService', () => {
  let service: ConfettiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
    });

    service = TestBed.inject(ConfettiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
