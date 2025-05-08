import {Component} from '@angular/core';
import {MatListModule} from '@angular/material/list';
import {MatDividerModule} from '@angular/material/divider';
import {MatCardHeader} from '@angular/material/card';

@Component({
  selector: 'app-nav',
  imports: [
    MatDividerModule,
    MatListModule,
  ],
  templateUrl: './app-nav.component.html',
  styleUrl: './app-nav.component.scss'
})
export class AppNavComponent {

}
