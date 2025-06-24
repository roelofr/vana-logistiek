import {Component, computed, ElementRef, inject, input, OnInit, output, signal, viewChild} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {VendorService} from '../../../services/vendor.service';
import {AsyncPipe} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {Vendor} from '../../../app.domain';

@Component({
  selector: 'app-new-ticket-vendor',
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    AsyncPipe,
    MatButtonModule
  ],
  templateUrl: './select-vendor.html',
  styleUrl: './select-vendor.css'
})
export class SelectVendor implements OnInit {
  readonly vendorService = inject(VendorService);

  readonly vendor = input<Vendor | null>(null);
  readonly vendorSelected = output<Vendor>();

  readonly vendorInput = viewChild.required<ElementRef<HTMLInputElement>>('input');
  readonly vendorControl = new FormControl<Vendor | null>(null, [Validators.required]);

  readonly vendors = signal<Vendor[]>([]);
  readonly vendorFilter = signal<string>('');
  readonly filteredVendors = computed(this.listVendors.bind(this))

  constructor() {
    this.vendors.set([]);
  }

  ngOnInit() {
    this.vendorService.getAll()
      .then(this.vendors.set);
  }

  setVendor() {
    if (!this.vendorControl.valid)
      return;

    if (!this.vendorControl.value)
      return;

    console.log('Vendor is set to %o', this.vendorControl.value);
    this.vendorSelected.emit(this.vendorControl.value as Vendor);
  }

  filterVendors() {
    this.vendorFilter.set(this.vendorInput().nativeElement.value ?? '')
  }

  displayVendor(vendor: Vendor): string {
    return vendor ? `${vendor.name} (${vendor.number})` : '';
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
