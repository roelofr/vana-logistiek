import {Component} from '@angular/core';
import {AppShellComponent} from '../../global/app-shell/app-shell.component';
import {AppDashboardComponent} from '../../global/app-dashboard/app-dashboard.component';

@Component({
  selector: 'app-homepage',
  imports: [
    AppShellComponent,
    AppDashboardComponent
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent {

}
