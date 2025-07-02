import {Component, computed, input} from '@angular/core';
import {TicketStatus, TicketStatusDetails} from '../../app.constants';
import {NgClass} from '@angular/common';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-ticket-status-card',
  imports: [
    NgClass,
    MatIconModule],
  templateUrl: './ticket-status-card.html',
  styleUrl: './ticket-status-card.scss'
})
export class TicketStatusCard {
  readonly status = input.required<TicketStatus>();

  readonly statusClass = computed(() => `ticket-status--${this.status().toLowerCase()}`);
  readonly icon = computed(() => TicketStatusDetails.get(this.status())?.icon ?? 'bug');
  readonly label = computed(() => TicketStatusDetails.get(this.status())?.label ?? 'Bug');
  readonly isLoading = computed(() => this.status() == TicketStatus.Loading);
}
