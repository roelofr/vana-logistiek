import { Component } from '@angular/core';
import {ClrIconModule, ClrLayoutModule} from '@clr/angular';
import {RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-ui',
  imports: [
    ClrIconModule,
    ClrLayoutModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './app-ui.component.html',
  styleUrl: './app-ui.component.css'
})
export class AppUiComponent {

}
