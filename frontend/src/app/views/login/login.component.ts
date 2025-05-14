import {afterNextRender, Component, signal} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {AuthService} from '../../services/global/auth.service';
import {merge} from 'rxjs';
import {AuthShellComponent} from '../../shared/auth-shell/auth-shell.component';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,

    FormsModule,
    RouterLink,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    AuthShellComponent,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  readonly form = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });

  readonly username = this.form.get('username');
  readonly password = this.form.get('password');
  readonly usernameError = signal('');
  readonly passwordError = signal('');
  private readonly loginError = signal<string | null>(null);

  constructor(private readonly authService: AuthService) {
    this.bindControls();
  }

  private bindControls() {
    if (!this.username || !this.password) {
      afterNextRender(this.bindControls.bind(this));
      return;
    }

    merge(
      this.form.statusChanges,
      this.username?.statusChanges,
      this.password?.statusChanges
    ).subscribe(this.updateValidity.bind(this));

    this.form.valueChanges.subscribe(() => this.loginError.set(null));
  }

  private updateValidity(): void {
    this.usernameError.set(this.determineUsernameError());
    this.passwordError.set(this.determinePasswordError());
  }

  private determineUsernameError(): string {
    if (this.loginError())
      return this.loginError() as string;

    if (!this.username?.touched)
      return '';

    if (this.username?.hasError('required'))
      return 'Gebruikersnaam is verplicht.';

    if (this.username?.hasError('email'))
      return 'Gebruikersnaam lijkt niet op een e-mailadres.';

    return '';
  }

  private determinePasswordError(): string {
    if (!this.password?.touched)
      return '';

    if (this.password?.hasError('required'))
      return 'Wachtwoord is verplicht.';

    if (this.password?.hasError('email'))
      return 'Wachtwoord lijkt niet op een e-mailadres.';

    return '';
  }

  handleSubmit() {
    console.log('Submit with data %o', this.form.value)

    this.form.markAllAsTouched();
    if (this.form.invalid) {
      this.updateValidity();
      return;
    }

    const {username, password} = this.form.value;

    console.log('Login as %s', username)

    this.authService.authenticate(username as string, password as string).subscribe(
      data => {
        if (data.ok)
          return;

        this.loginError.set(data.error);
      }
    );
  }
}
