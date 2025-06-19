import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CreateTicketComponent} from './create-ticket.component';
import {provideZonelessChangeDetection} from '@angular/core';
import {provideTestAppIcons} from '../../app.icons';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('CreateTicketComponent', () => {
  let component: CreateTicketComponent;
  let fixture: ComponentFixture<CreateTicketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        provideTestAppIcons(),
        provideHttpClient(withFetch()),
        provideHttpClientTesting(),
      ],
      imports: [CreateTicketComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateTicketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
