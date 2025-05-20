import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AuthShellComponent} from './auth-shell.component';
import {ComponentRef, provideExperimentalZonelessChangeDetection} from '@angular/core';

describe('AuthShellComponent', () => {
  let component: AuthShellComponent;
  let ref: ComponentRef<AuthShellComponent>;
  let fixture: ComponentFixture<AuthShellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
      ],
      imports: [AuthShellComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AuthShellComponent);
    component = fixture.componentInstance;

    ref = fixture.componentRef;
    ref.setInput('cardTitle', 'Card Title');

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
