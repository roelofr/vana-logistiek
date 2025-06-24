import {Component, computed, signal, viewChild} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatStepper, MatStepperModule} from '@angular/material/stepper';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {SelectVendor} from './select-vendor/select-vendor';
import {MatIconModule} from '@angular/material/icon';
import {Vendor} from '../../app.domain';

@Component({
  selector: 'app-create-ticket',
  imports: [
    MatButtonModule,
    MatStepperModule,
    FormsModule,
    MatIconModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    SelectVendor,
  ],
  templateUrl: './create-ticket.component.html',
  styleUrl: './create-ticket.component.css',
})
export class CreateTicketComponent {
  readonly stepper = viewChild.required<MatStepper>('stepper');

  readonly vendor = signal<Vendor | null>(null);
  readonly type = signal<string | null>(null);
  readonly summary = signal<string | null>(null);

  readonly currentStep = signal(0);

  readonly vendorComplete = computed(() => this.vendor() != null);
  readonly typeCompleted = computed(() => this.type() != null);

  vendorSelected(vendor: Vendor): void {
    this.vendor.set(vendor);

    requestAnimationFrame(() => this.currentStep.set(1));
  }
}
