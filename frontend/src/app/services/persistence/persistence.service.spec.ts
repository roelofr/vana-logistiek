import { TestBed } from '@angular/core/testing';

import { LocalStoragePersistenceService } from './persistence.service';

describe('LocalStoragePersistenceService', () => {
  let service: LocalStoragePersistenceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocalStoragePersistenceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
