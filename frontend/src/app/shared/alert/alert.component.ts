import {Component, computed, input} from '@angular/core';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-alert',
  imports: [
    NgClass
  ],
  templateUrl: './alert.component.html',
  styleUrl: './alert.component.scss'
})
export class AlertComponent {
  readonly type = input<string>('danger');

  readonly classList = computed(() => ({
    'alert': true,
    'alert-info': this.type() == 'info',
    'alert-danger': this.type() == 'danger',
    'alert-warning': this.type() == 'warning',
    'alert-success': this.type() == 'success',
  }))
}
