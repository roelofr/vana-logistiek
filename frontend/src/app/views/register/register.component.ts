import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {PhoneNumberValidator} from "../../shared/validator/phone-number.directive";
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {AuthShellComponent} from '../../shared/auth-shell/auth-shell.component';
import {AuthService} from '../../services/global/auth.service';

@Component({
  selector: 'app-register',
  imports: [
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    AuthShellComponent
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
  readonly passwordMinLength = 8;
  readonly form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    username: new FormControl('', [Validators.required, Validators.email]),
    phone: new FormControl('', [Validators.required, PhoneNumberValidator]),
    password: new FormControl('', [Validators.required, Validators.minLength(
      this.passwordMinLength)])
  });

  constructor(private readonly authService: AuthService) {
    //
  }

  ngOnInit(): void {
    if (Object.hasOwn(navigator, "credentials"))
      this.loadWebauthnChallenge();
  }

  handleSubmit(): void {
    // TODO
  }

  private loadWebauthnChallenge(): void {
    this.authService.getWebauthnChallenge()
      .subscribe(challenge => {
        console.log('Challenge is %o', challenge);
      })
  }
}
