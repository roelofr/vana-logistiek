import {Component, inject, OnInit, signal} from '@angular/core';
import {TicketService} from '../../../services/ticket.service';
import {Ticket} from '../../../app.domain';
import {PageTitle} from '../../../components/page-title/page-title';
import {TicketTable} from '../../../components/ticket-table/ticket-table';
import {MatProgressBar} from '@angular/material/progress-bar';

@Component({
  selector: 'app-ticket-index',
  imports: [
    PageTitle,
    TicketTable,
    MatProgressBar
  ],
  templateUrl: './index.html',
  styleUrl: './index.scss'
})
export class Index implements OnInit {
  readonly ticketService = inject(TicketService);

  readonly tickets = signal<Ticket[]>([]);
  readonly ticketsLoading = signal(false);

  ngOnInit() {
    this.updateTickets();
  }

  async updateTickets(): Promise<void> {
    this.ticketsLoading.set(true);

    try {
      const tickets = await this.ticketService.getAll();
      this.tickets.set(tickets);
    } catch (error) {
      console.error("Failed to fetch ticket: %o", error);
    } finally {
      this.ticketsLoading.set(false);
    }
  }
}
