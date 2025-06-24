import {Component, input, output} from '@angular/core';

@Component({
  selector: 'app-select-type',
  imports: [],
  templateUrl: './select-type.html',
  styleUrl: './select-type.scss'
})
export class SelectType {
  readonly selectedType = input<string | null>();
  readonly typeSelected = output<string>();
}
