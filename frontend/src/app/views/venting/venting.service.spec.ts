import { TestBed } from '@angular/core/testing';

import { VentingService } from './venting.service';

describe('VentingService', () => {
  let service: VentingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VentingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
