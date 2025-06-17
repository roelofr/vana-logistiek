import {TestBed} from '@angular/core/testing';

import {TemplatePageTitleStrategy} from './template-page-title-strategy.service';
import {provideZonelessChangeDetection} from '@angular/core';

describe('MetadataService', () => {
  let service: TemplatePageTitleStrategy;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
    });
    service = TestBed.inject(TemplatePageTitleStrategy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
