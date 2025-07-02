import {Component, input} from '@angular/core';
import {VendorRef} from '../../app.domain';

@Component({
  selector: 'app-vendor-card',
  imports: [],
  templateUrl: './vendor-card.html',
  styleUrl: './vendor-card.scss'
})
export class VendorCard {
  readonly vendor = input.required<VendorRef>();
}
