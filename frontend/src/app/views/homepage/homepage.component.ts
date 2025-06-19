import {Component} from '@angular/core';
import {DashboardComponent} from '../../components/dashboard/dashboard.component';

@Component({
  selector: 'app-homepage',
  imports: [DashboardComponent],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css',
})
export class HomepageComponent {
}
