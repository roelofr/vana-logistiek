import {Component, computed, inject, signal, viewChild} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatStepper, MatStepperModule} from '@angular/material/stepper';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {SelectVendor} from './select-vendor/select-vendor';
import {MatIconModule} from '@angular/material/icon';
import {TicketDetails, Vendor} from '../../app.domain';
import {SelectType} from './select-type/select-type';
import {TicketType, TicketTypeDetails} from '../../app.constants';
import {Details} from './details/details';
import {TicketService} from '../../services/ticket.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

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
    SelectType,
    Details,
  ],
  templateUrl: './create-ticket.component.html',
  styleUrl: './create-ticket.component.css',
})
export class CreateTicketComponent {
  private readonly ticketService = inject(TicketService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  readonly stepper = viewChild.required<MatStepper>('stepper');

  readonly vendor = signal<Vendor | null>(null);
  readonly type = signal<TicketType | null>(null);
  readonly details = signal<TicketDetails | null>(null);
  readonly summary = signal<string | null>(null);

  readonly currentStep = signal(0);
  readonly isLoading = signal(false);
  readonly ticketCreateError = signal<Error | null>(null);

  readonly vendorComplete = computed(() => this.vendor() != null);
  readonly typeCompleted = computed(() => this.type() != null);
  readonly typeDetails = computed(() => this.type() != null ? TicketTypeDetails.get(this.type() as TicketType) : null)


  vendorSelected(vendor: Vendor): void {
    this.vendor.set(vendor);

    requestAnimationFrame(() => this.currentStep.set(1));
  }

  typeSelected(type: TicketType): void {
    this.type.set(type);

    requestAnimationFrame(() => this.currentStep.set(2))
  }

  detailsSelected(details: TicketDetails): void {
    this.details.set(details);
    this.summary.set(details.summary);

    this.createTicket();
  }

  async createTicket(): Promise<void> {
    this.isLoading.set(true);

    try {
      const ticket = await this.ticketService.createTicket(
        this.type()!,
        this.vendor()!,
        this.details()!
      );

      this.snackBar.open(`Ticket ${ticket.id} aangemaakt!`, undefined, {
        duration: 5000,
        politeness: 'assertive',
      });

      this.router.navigate(['/ticket', ticket.id]);
    } catch (error) {
      this.ticketCreateError.set(error as Error);
      this.isLoading.set(false);
    }


  }
}
