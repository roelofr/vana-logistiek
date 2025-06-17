import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppDashboardComponent} from './app-dashboard.component';
import {provideZonelessChangeDetection} from '@angular/core';

describe('AppDashboardComponent', () => {
  let component: AppDashboardComponent;
  let fixture: ComponentFixture<AppDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
      imports: [AppDashboardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AppDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should compile', () => {
    expect(component).toBeTruthy();
  });
});
