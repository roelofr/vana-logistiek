import {TestBed} from '@angular/core/testing';

import {PersistenceService} from './persistence.service';
import {provideZonelessChangeDetection} from '@angular/core';

describe('PersistenceService', () => {
  let service: PersistenceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
    });
    service = TestBed.inject(PersistenceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
