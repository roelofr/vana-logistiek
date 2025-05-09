import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {AppShellComponent} from '../../global/app-shell/app-shell.component';

@Component({
  selector: 'app-user-shell',
  imports: [
    AppShellComponent,
    RouterOutlet,
  ],
  templateUrl: './user-shell.component.html'
})
export class UserShellComponent {

}
