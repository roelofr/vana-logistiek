import {TestBed} from '@angular/core/testing';

import {TemplatePageTitleStrategy} from './template-page-title-strategy.service';

describe('MetadataService', () => {
  let service: TemplatePageTitleStrategy;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TemplatePageTitleStrategy);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
