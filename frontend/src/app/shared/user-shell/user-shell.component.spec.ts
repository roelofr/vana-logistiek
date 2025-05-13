import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserShellComponent} from './user-shell.component';
import {provideExperimentalZonelessChangeDetection} from '@angular/core';
import {provideTestAppIcons} from '../../app.icons';
import {provideRouter} from '@angular/router';

describe('UserShellComponent', () => {
  let component: UserShellComponent;
  let fixture: ComponentFixture<UserShellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideRouter([{path: '**', component: UserShellComponent}]),
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
