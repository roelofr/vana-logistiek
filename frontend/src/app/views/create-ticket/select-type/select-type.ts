import {Component, input, output} from '@angular/core';
import {TicketType, TicketTypeDetails} from '../../../app.constants';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatRadioModule} from '@angular/material/radio';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';

interface TypeDetailsWithType {
  type: TicketType,
  label: string,
  icon: string,
  summary: string,
}

@Component({
  selector: 'app-new-ticket-type',
  imports: [
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatRadioModule,
    MatIconModule,
    ReactiveFormsModule,
  ],
  templateUrl: './select-type.html',
  styleUrl: './select-type.css'
})
export class SelectType {
  readonly selectedType = input<TicketType | null>();
  readonly typeSelected = output<TicketType>();
  readonly back = output<void>();

  readonly typeControl = new FormControl<TicketType | null>(null, [Validators.required]);

  readonly typeMap: TypeDetailsWithType[] = Array.from(TicketTypeDetails.entries())
    .map(([type, data]) => ({...data, type}))

  setType(): void {
    if (this.typeControl.invalid)
      return;

    this.typeSelected.emit(this.typeControl.value as TicketType);
  }
}
