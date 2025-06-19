import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppNavComponent} from './app-nav.component';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideTestAppIcons} from '../../app.icons';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('AppNavComponent', () => {
  let component: AppNavComponent;
  let fixture: ComponentFixture<AppNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([{path: '**', component: AppNavComponent}]),
        provideTestAppIcons(),
        provideHttpClient(withFetch()),
        provideHttpClientTesting(),
      ],
      imports: [AppNavComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AppNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
