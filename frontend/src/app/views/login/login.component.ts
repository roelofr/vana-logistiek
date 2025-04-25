import { Component } from '@angular/core';
import {ClarityModule} from '@clr/angular';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [
    ClarityModule,
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  form = {
    type: 'local',
    username: '',
    password: '',
    rememberMe: false,
  };
}
