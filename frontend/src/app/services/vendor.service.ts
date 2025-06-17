import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {firstValueFrom, lastValueFrom, timeout} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class VendorService {
  private readonly http = inject(HttpClient);

  private vendorList = signal<Vendor[] | null>(null);

  async fetchList(): Promise<Vendor[]> {
    if (this.vendorList() != null)
      return this.vendorList() as Vendor[];

    const request = this.http.get<Vendor[]>('/vendor/')
      .pipe(timeout(5_000));

    try {
      const vendorList = await lastValueFrom(request)
      this.vendorList.set(vendorList);
      return vendorList;
    } catch (error) {
      console.error("Failed to fetch vendors; %o", error);
      return [];
    }
  }

  async getAll(): Promise<Vendor[]> {
    return this.fetchList();
  }

  async getById(id: string): Promise<Vendor> {
    const request = this.http.get<Vendor>(`${this.vendorList}/${id}`)
      .pipe(timeout(5_000))

    try {
      return firstValueFrom(request);
    } catch (error) {
      throw new Error(`Failed to fetch Vendor: ${id}`, {cause: error});
    }
  }
}
