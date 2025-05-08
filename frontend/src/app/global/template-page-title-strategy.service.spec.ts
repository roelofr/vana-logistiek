import { TestBed } from '@angular/core/testing';

import { TemplatePageTitleStrategy } from './template-page-title-strategy.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';

describe('MetadataService', () => {
  let service: TemplatePageTitleStrategy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideExperimentalZonelessChangeDetection()],
    });
    service = TestBed.inject(TemplatePageTitleStrategy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
