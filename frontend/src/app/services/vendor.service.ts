import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom, lastValueFrom, timeout} from 'rxjs';
import {DateTime, Duration} from 'luxon';
import {Vendor} from '../app.domain';

@Injectable({
  providedIn: 'root'
})
export class VendorService {
  private readonly cacheLifetime: Duration = Duration.fromISO('PT6H');
  private readonly http = inject(HttpClient);

  private vendorCache: Vendor[] | null = null;
  private vendorCacheExpire: DateTime = DateTime.fromMillis(0);

  async fetchList(): Promise<Vendor[]> {
    if (this.vendorCache != null && this.vendorCacheExpire > DateTime.now())
      return this.vendorCache

    const request = this.http.get<Vendor[]>('/api/vendor/')
      .pipe(timeout(5_000));

    try {
      const vendors = await lastValueFrom(request)

      this.vendorCache = vendors;
      this.vendorCacheExpire = DateTime.now().plus(this.cacheLifetime)

      return vendors;
    } catch (error) {
      console.error("Failed to fetch vendors; %o", error);
      return [];
    }
  }

  async getAll(): Promise<Vendor[]> {
    return await this.fetchList();
  }

  async getById(id: string): Promise<Vendor> {
    const request = this.http.get<Vendor>(`/api/vendor/${id}`)
      .pipe(timeout(5_000))

    try {
      return firstValueFrom(request);
    } catch (error) {
      throw new Error(`Failed to fetch Vendor: ${id}`, {cause: error});
    }
  }

  async getByIdCached(id: number): Promise<Vendor | null> {
    return (await this.getAll()).find(vendor => vendor.id = id) ?? null;
  }
}
