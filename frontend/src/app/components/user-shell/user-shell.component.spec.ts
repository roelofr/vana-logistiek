import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserShellComponent} from './user-shell.component';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideTestAppIcons} from '../../app.icons';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('UserShellComponent', () => {
  let component: UserShellComponent;
  let fixture: ComponentFixture<UserShellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([{path: '**', component: UserShellComponent}]),
        provideHttpClient(withFetch()),
        provideHttpClientTesting(),
        provideTestAppIcons(),
      ],
      imports: [UserShellComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(UserShellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
