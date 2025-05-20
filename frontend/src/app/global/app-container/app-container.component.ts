import {Component} from '@angular/core';

@Component({
  selector: 'app-container',
  styleUrl: './app-container.component.scss',
  template: '<div class="container"><ng-content /></div>',
})
export class AppContainerComponent {
  //
}
