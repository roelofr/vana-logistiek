import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HomepageComponent} from './homepage.component';
import {provideRouter} from '@angular/router';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideTestAppIcons} from '../../app.icons';

describe('HomepageComponent', () => {
  let component: HomepageComponent;
  let fixture: ComponentFixture<HomepageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideRouter([{path: '**', component: HomepageComponent}]),
        provideTestAppIcons(),
      ],
      imports: [HomepageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(HomepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
