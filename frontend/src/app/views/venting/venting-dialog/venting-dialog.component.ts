import {Component, inject, OnInit, signal} from '@angular/core';
import {VentingService} from '../venting.service';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDialogModule} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {ConfettiService} from '../../../global/confetti/confetti.service';

@Component({
  selector: 'app-venting-dialog',
  imports: [
    MatDialogModule,
    MatProgressSpinnerModule,
    MatButtonModule
  ],
  templateUrl: './venting-dialog.component.html',
  styleUrl: './venting-dialog.component.scss'
})
export class VentingDialogComponent implements OnInit {
  readonly currentStep = signal(1);
  readonly complaintResolve = signal('');
  private readonly service = inject(VentingService);
  private readonly confetti = inject(ConfettiService);

  ngOnInit() {
    this.currentStep.set(1);
    this.complaintResolve.set('');
  }

  async handleClickContinue() {
    this.currentStep.set(2);

    const reply = await this.service.submitComplaint()

    this.currentStep.set(3);

    this.complaintResolve.set(reply);
  }
}
