import {Component, input, OnChanges, OnInit, signal, SimpleChanges, viewChild} from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {Ticket} from '../../app.domain';
import {TicketStatusCard} from '../ticket-status-card/ticket-status-card';
import {VendorCard} from '../vendor-card/vendor-card';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatSort, MatSortModule} from '@angular/material/sort';
import {ticketStatusToOrder, vendorToSortableNumber} from '../../app.util';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-ticket-table',
  imports: [
    MatTableModule, MatSortModule, MatPaginatorModule,
    TicketStatusCard,
    VendorCard,
    MatProgressBar, RouterLink,
  ],
  templateUrl: './ticket-table.html',
  styleUrl: './ticket-table.css'
})
export class TicketTable implements OnInit, OnChanges {
  readonly tickets = input.required<Ticket[]>();

  readonly sortable = signal(true);
  readonly pagination = signal(true);

  readonly paginator = viewChild.required(MatPaginator);
  readonly sort = viewChild.required(MatSort);

  readonly ticketDataSource = new MatTableDataSource<Ticket>([]);

  displayedColumns: string[] = ['id', 'description', 'status', 'vendor'];

  ngOnInit() {
    this.ticketDataSource.data = this.tickets();
    this.ticketDataSource.sort = this.sort()!;
    this.ticketDataSource.paginator = this.paginator()!;

    this.ticketDataSource.sortingDataAccessor = this.sortTicket.bind(this);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (Object.hasOwn(changes, 'tickets') && !changes['tickets'].isFirstChange()) {
      this.ticketDataSource.data = this.tickets();
      this.paginator()!.firstPage();
    }
  }

  sortTicket(ticket: Ticket, header: string): string | number {
    if (header == 'id')
      return ticket.id;

    if (header == 'vendor')
      return vendorToSortableNumber(ticket.vendor);

    if (header == 'status')
      return ticketStatusToOrder(ticket.status);

    return `${ticket[header as keyof Ticket]}`.toLowerCase();
  }
}
