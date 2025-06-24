import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TicketStatusCard} from './ticket-status-card';
import {provideZonelessChangeDetection} from '@angular/core';
import {TicketStatus, TicketStatusDetails} from '../../app.constants';

describe('TicketStatusCard', () => {
  let component: TicketStatusCard;
  let fixture: ComponentFixture<TicketStatusCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
      imports: [TicketStatusCard]
    })
      .compileComponents();

    fixture = TestBed.createComponent(TicketStatusCard);

    fixture.componentRef.setInput('status', TicketStatus.Created);

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();

    expect(component.statusClass()).toContain('-created');
    expect(component.label()).toBe(TicketStatusDetails.get(TicketStatus.Created)!.label);
    expect(component.icon()).toBe(TicketStatusDetails.get(TicketStatus.Created)!.icon);
  });
});
