import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppShellComponent } from './app-shell.component';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';
import { provideTestAppIcons } from '../../app.icons';
import { provideHttpClient, withFetch } from '@angular/common/http';
import {provideRouter} from '@angular/router';

describe('AppShellComponent', () => {
  let component: AppShellComponent;
  let fixture: ComponentFixture<AppShellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideRouter([{ path: '**', component: AppShellComponent }]),
        provideHttpClient(withFetch()),
        provideTestAppIcons(),
      ],
      imports: [AppShellComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AppShellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
