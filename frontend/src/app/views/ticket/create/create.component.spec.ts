import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CreateComponent} from './create.component';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideTestAppIcons} from '../../../app.icons';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('Tickets/CreateComponent', () => {
  let component: CreateComponent;
  let fixture: ComponentFixture<CreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideTestAppIcons(),
        provideHttpClient(withFetch()),
        provideHttpClientTesting(),
      ],
      imports: [CreateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
