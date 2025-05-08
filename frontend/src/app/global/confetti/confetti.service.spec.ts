import { TestBed } from '@angular/core/testing';

import { ConfettiService, jsConfetti } from './confetti.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';

describe('ConfettiService', () => {
  let service: ConfettiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideExperimentalZonelessChangeDetection()],
    });

    service = TestBed.inject(ConfettiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should show confetti when prompted', () => {
    expect(service.confettiShowing()).toBeFalsy();

    service.dispenseConfetti();

    expect(service.confettiShowing()).toBeTruthy();

    jsConfetti.clearCanvas();

    requestAnimationFrame(() => {
      expect(service.confettiShowing()).toBeFalsy();
    });
  });
});
