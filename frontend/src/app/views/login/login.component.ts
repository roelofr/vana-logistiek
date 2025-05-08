import {Component, signal, Signal, viewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    RouterLink,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private form = viewChild<HTMLFormElement>('form');

  items: Signal<number[]>;

  constructor() {
    this.items = signal(Array(12).fill(null).map((_, i) => 1 + i));
  }

  handleSubmit(event: SubmitEvent) {
    event.preventDefault();

    console.error('Form data is %o, caught %O', this.form(), event)
  }
}
