import {Component, input} from '@angular/core';

@Component({
  selector: 'app-page-title',
  imports: [],
  templateUrl: './page-title.html',
  styleUrl: './page-title.css'
})
export class PageTitle {
  readonly title = input.required<string>();
  readonly subtitle = input<string | null>(null);
}
