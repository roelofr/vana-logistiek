import {Component, computed, inject, input, OnInit, output, signal} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {VendorService} from '../../../services/vendor.service';
import {AsyncPipe} from '@angular/common';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-select-vendor',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    AsyncPipe,
    MatButton
  ],
  templateUrl: './select-vendor.html',
  styleUrl: './select-vendor.scss'
})
export class SelectVendor implements OnInit {
  readonly vendorService = inject(VendorService);

  readonly vendor = input<Vendor | null>(null);
  readonly vendorSelected = output<Vendor>();

  readonly vendorInput = new FormControl('');

  readonly vendors = signal<Vendor[]>([]);
  readonly vendorFilter = signal<string>('');
  readonly filteredVendors = computed(this.listVendors.bind(this))

  constructor() {
    this.vendorInput.valueChanges
      .subscribe((value) => this.vendorFilter.set(value || ''))
  }

  async ngOnInit() {
    this.vendors.set(await this.vendorService.getAll());
  }

  setVendor() {
    if (this.vendorInput.value)
      this.vendorSelected.emit(this.vendorInput.value as unknown as Vendor);

    alert('No vendor set');
  }

  displayVendor(vendor: any): string {
    return vendor != null ? vendor.name : '';
  }

  private async listVendors() {
    const cleanVendorFilter = this.vendorFilter().trim().toLowerCase();
    if (cleanVendorFilter.length == 0)
      return this.vendors();

    const vendorFilterWords = cleanVendorFilter.split(/[^a-z0-9]+/)

    return this.vendors().filter(row => {
      for (const word of vendorFilterWords)
        if (!row.name.toLowerCase().match(word) && !row.number.includes(word))
          return false;

      return true;
    })
  }
}
