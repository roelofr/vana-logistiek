import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppShellComponent} from './app-shell.component';
import {provideExperimentalZonelessChangeDetection} from '@angular/core';
import {provideAppIcons, provideTestAppIcons} from '../../app.icons';
import {provideHttpClient, withFetch} from '@angular/common/http';

describe('AppShellComponent', () => {
  let component: AppShellComponent;
  let fixture: ComponentFixture<AppShellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideHttpClient(withFetch()),
        provideTestAppIcons(),
      ],
      imports: [AppShellComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AppShellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
