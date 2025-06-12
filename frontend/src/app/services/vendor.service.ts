import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {finalize, timeout} from 'rxjs';
import {rejects} from 'node:assert';

@Injectable({
  providedIn: 'root'
})
export class VendorService {
  private vendorList: Vendor[] = signal(null);

  constructor(private readonly http: HttpClient) {
    //
  }

  fetch$(): Observable<Vendor[]> {
    if (this.vendorList != null)
      return Observable.of(this.vendorList);

    return this.http.get<Vendor[]>('/vendor/')
      .pipe(
        timeout(5_000),
        finalize((response) => (this.vendorList = response))
      );
  }

  getAll(): Promise<Vendor[]> {
    return new Promise(resolve => {
      Observable.of(() => this.fetch$())
        .subscribe(resolve);
    });
  }

  getById(id: string): Promise<Vendor> {
    return new Promise<Vendor>(resolve => {
      this.http.get<Vendor>(`${this.vendorList}/${id}`)
        .pipe(timeout(5_000))
        .subscribe({
          next: vendor => resolve(vendor),
          error: error => rejects(error),
        });

    })
  }
}
