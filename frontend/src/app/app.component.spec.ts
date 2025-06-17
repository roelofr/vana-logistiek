import {TestBed} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideTestAppIcons} from './app.icons';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideTestAppIcons(),
      ],
      imports: [AppComponent],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
