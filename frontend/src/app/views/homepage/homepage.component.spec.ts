import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HomepageComponent} from './homepage.component';
import {provideRouter} from '@angular/router';
import {provideExperimentalZonelessChangeDetection} from '@angular/core';
import {provideTestAppIcons} from '../../app.icons';

describe('HomepageComponent', () => {
  let component: HomepageComponent;
  let fixture: ComponentFixture<HomepageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideRouter([{path: '**', component: HomepageComponent}]),
        provideTestAppIcons(),
      ],
      imports: [HomepageComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(HomepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
