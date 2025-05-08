import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppNavComponent} from './app-nav.component';
import {provideExperimentalZonelessChangeDetection} from '@angular/core';

describe('AppNavComponent', () => {
  let component: AppNavComponent;
  let fixture: ComponentFixture<AppNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection()
      ],
      imports: [AppNavComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AppNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
