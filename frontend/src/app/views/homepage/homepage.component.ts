import {Component, inject, OnInit, signal} from '@angular/core';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {PageTitle} from '../../components/page-title/page-title';
import {TicketService} from '../../services/ticket.service';
import {Ticket} from '../../app.domain';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {TicketTable} from '../../components/ticket-table/ticket-table';
import {MatProgressBar} from '@angular/material/progress-bar';

@Component({
  selector: 'app-homepage',
  imports: [
    MatTabsModule,
    MatTableModule,
    MatProgressSpinnerModule,
    PageTitle,
    TicketTable,
    MatProgressBar,
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent implements OnInit {
  readonly ticketService = inject(TicketService);

  readonly tickets = signal<Ticket[]>([]);
  readonly ticketsLoading = signal(false);

  ngOnInit() {
    this.loadTickets();
  }

  async loadTickets(): Promise<void> {
    this.ticketsLoading.set(true);

    try {
      this.tickets.set(await this.ticketService.getAll());
    } finally {
      this.ticketsLoading.set(false);
    }
  }
}
