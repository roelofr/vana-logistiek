import {Component} from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatDividerModule} from '@angular/material/divider';
import {RouterModule} from '@angular/router';

@Component({
  selector: 'app-nav',
  imports: [
    MatDividerModule,
    MatListModule,
    RouterModule,
  ],
  templateUrl: './app-nav.component.html',
  styleUrl: './app-nav.component.scss',
})
export class AppNavComponent {
}
