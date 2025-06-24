import {inject, Injectable} from '@angular/core';
import {DateTime, Duration} from 'luxon';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom, lastValueFrom, timeout} from 'rxjs';
import {Ticket} from '../app.domain';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private readonly cacheLifetime: Duration = Duration.fromISO('PT6H');
  private readonly http = inject(HttpClient);

  private ticketCache: Ticket[] | null = null;
  private ticketCacheExpire: DateTime = DateTime.fromMillis(0);
  
  async fetchList(): Promise<Ticket[]> {
    if (this.ticketCache != null && this.ticketCacheExpire > DateTime.now())
      return this.ticketCache

    const request = this.http.get<Ticket[]>('/api/ticket/')
      .pipe(timeout(5_000));

    try {
      const tickets = await lastValueFrom(request)

      this.ticketCache = tickets;
      this.ticketCacheExpire = DateTime.now().plus(this.cacheLifetime)

      const sortedTickets = Array.from(tickets).sort((a, b) => a.id - b.id)

      console.info("Fetched %d tickets, from ID %s - %s", tickets.length, sortedTickets[0].id, sortedTickets.findLast(() => true)?.id);

      return tickets;
    } catch (error) {
      console.error("Failed to fetch tickets; %o", error);
      return [];
    }
  }

  async getAll(): Promise<Ticket[]> {
    return await this.fetchList();
  }

  async getById(id: string): Promise<Ticket> {
    const request = this.http.get<Ticket>(`/api/ticket/${id}`)
      .pipe(timeout(5_000))

    try {
      return firstValueFrom(request);
    } catch (error) {
      throw new Error(`Failed to fetch Ticket: ${id}`, {cause: error});
    }
  }

  async getByIdCached(id: number): Promise<Ticket | null> {
    return (await this.getAll()).find(vendor => vendor.id = id) ?? null;
  }
}
