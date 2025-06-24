import {AfterViewInit, Component, inject, signal} from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {PageTitle} from '../../components/page-title/page-title';
import {TicketService} from '../../services/ticket.service';
import {Ticket} from '../../app.domain';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {TicketStatusCard} from '../../components/ticket-status-card/ticket-status-card';
import {VendorCard} from '../../components/vendor-card/vendor-card';

@Component({
  selector: 'app-homepage',
  imports: [
    MatTabsModule,
    MatTableModule,
    MatProgressSpinnerModule,
    PageTitle,
    TicketStatusCard,
    VendorCard,
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent implements AfterViewInit {
  readonly ticketService = inject(TicketService);

  readonly ticketDataSource = signal<MatTableDataSource<Ticket>>(new MatTableDataSource());
  readonly isLoading = signal<boolean>(true);

  displayedColumns: string[] = ['id', 'description', 'status', 'vendor'];

  ngAfterViewInit(): void {
    this.downloadData();
  }

  private async downloadData(): Promise<void> {
    this.isLoading.set(true);

    try {
      const tickets = await this.ticketService.getAll();

      this.ticketDataSource().data = tickets;
    } finally {
      this.isLoading.set(false)
    }
  }
}
