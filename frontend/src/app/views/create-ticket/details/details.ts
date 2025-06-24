import {Component, input, output} from '@angular/core';
import {TicketType} from '../../../app.constants';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {TicketDetails} from '../../../app.domain';

@Component({
  selector: 'app-new-ticket-details',
  imports: [
    FormsModule,
    MatButtonModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  templateUrl: './details.html',
  styleUrl: './details.scss'
})
export class Details {
  readonly type = input.required<TicketType>();
  readonly detailsSelected = output<TicketDetails>();

  readonly summaryControl = new FormControl('', [Validators.required]);

  setDetails() {
    if (!this.summaryControl.valid)
      return;

    this.detailsSelected.emit({
      summary: this.summaryControl.value!,
    });
  }

}
