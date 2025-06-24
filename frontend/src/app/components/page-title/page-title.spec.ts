import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PageTitle} from './page-title';
import {provideZonelessChangeDetection} from '@angular/core';

describe('PageTitle', () => {
  let component: PageTitle;
  let fixture: ComponentFixture<PageTitle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
      imports: [PageTitle]
    })
      .compileComponents();

    fixture = TestBed.createComponent(PageTitle);

    fixture.componentRef.setInput('title', 'Hello World');

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {

    expect(component).toBeTruthy();

    expect(component.title()).toBe('Hello World');
  });
});
