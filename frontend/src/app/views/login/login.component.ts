import {Component, viewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private form = viewChild<HTMLFormElement>('form');

  handleSubmit(event: SubmitEvent) {
    event.preventDefault();

    console.error('Form data is %o, caught %O', this.form(), event)
  }
}
