import {Component, computed, inject, OnDestroy, signal} from '@angular/core';
import {TicketService} from '../../../services/ticket.service';
import {PageTitle} from '../../../components/page-title/page-title';
import {Ticket} from '../../../app.domain';
import {ActivatedRoute} from '@angular/router';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {VendorCard} from '../../../components/vendor-card/vendor-card';
import {TicketStatusCard} from '../../../components/ticket-status-card/ticket-status-card';
import {TicketStatus} from '../../../app.constants';

@Component({
  selector: 'app-ticket-show',
  imports: [
    PageTitle,
    MatProgressBarModule,
    VendorCard,
    TicketStatusCard
  ],
  templateUrl: './show.component.html',
  styleUrl: './show.component.scss'
})
export class ShowComponent implements OnDestroy {
  private readonly ticketService = inject(TicketService);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly ticketId = signal<number>(-1);

  readonly ticketSummary = signal<null | Ticket>(null);
  readonly ticket = signal<null | Ticket>(null);
  readonly anyTicket = computed(() => {
    const fullTicket = this.ticket()
    const ticketSummary = this.ticketSummary()
    return fullTicket || ticketSummary;
  });

  readonly isLoading = computed(() => this.ticket() == null);
  readonly pageTitle = computed(() => this.buildPageTitle())
  readonly pageSubtitle = computed(() => this.buildPageSubtitle())

  constructor() {
    this.activatedRoute.params.subscribe((params) => {
      const ticketId = Number.parseInt(params['ticketId'], 10);
      if (ticketId == this.ticketId())
        return;

      this.ticketId.set(ticketId);
      this.loadData();
    });
  }

  ngOnDestroy() {
    this.clearData();
  }

  /**
   * Always reset when the views is initialised, which is before it's shown to the user.
   */
  private clearData() {
    this.ticket.set(null);
    this.ticketSummary.set(null);
  }

  private buildPageTitle() {
    const ticketId = this.ticketId();
    if (!ticketId)
      return 'Loading...';

    const ticket = this.ticket() ?? this.ticketSummary();
    if (!ticket)
      return `#${ticketId} - ...`

    return `#${ticketId} - ${ticket.description}`
  }

  private buildPageSubtitle() {
    const ticket = this.anyTicket();
    if (ticket == null)
      return null;

    return `Ticket #${ticket.id} voor ${ticket.vendor.name} (${ticket.vendor.number})`
  }

  private loadData() {
    const id = this.ticketId();
    if (!id)
      return;

    console.log('Load data');

    this.ticketService.getById(this.ticketId().toFixed(0))
      .then(ticket => this.ticket.set(ticket));

    this.ticketService.getByIdCached(id)
      .then(ticket => this.ticketSummary.set(ticket));
  }

  protected readonly TicketStatus = TicketStatus;
}
