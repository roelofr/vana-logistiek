import {afterNextRender, Component, inject, signal} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators,} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {AuthError, AuthService} from '../../services/global/auth.service';
import {merge} from 'rxjs';
import {AuthShellComponent} from '../../components/auth-shell/auth-shell.component';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  imports: [
    AuthShellComponent,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  readonly form = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });
  readonly username = this.form.get('username');
  readonly password = this.form.get('password');
  readonly usernameError = signal('');
  readonly passwordError = signal('');
  readonly loginError = signal<string | null>(null);
  private readonly authService = inject(AuthService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  constructor() {
    this.bindControls();
  }

  async handleSubmit() {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      this.updateValidity();
      return;
    }

    const {username, password} = this.form.value as Record<string, string>;

    this.loginError.set(null);

    try {
      await this.authService.login(username, password);

      this.snackBar.open(`Ingelogd als ${this.authService.name}`, undefined, {
        duration: 5000,
        politeness: 'assertive',
      });

      this.router.navigate([''], {
        replaceUrl: false,
        onSameUrlNavigation: 'reload'
      });
    } catch (error) {
      if (!(error instanceof AuthError))
        return this.loginError.set("Unknown error");

      console.error("auth error = %o", error.message);
      this.loginError.set(error.message);
    }
  }

  private bindControls() {
    if (!this.username || !this.password) {
      afterNextRender(this.bindControls.bind(this));
      return;
    }

    merge(
      this.form.statusChanges,
      this.username?.statusChanges,
      this.password?.statusChanges,
    ).subscribe(this.updateValidity.bind(this));

    this.form.valueChanges.subscribe(() => this.loginError.set(null));
  }

  private updateValidity(): void {
    this.usernameError.set(this.determineUsernameError());
    this.passwordError.set(this.determinePasswordError());
  }

  private determineUsernameError(): string {
    if (this.loginError()) return this.loginError() as string;

    if (!this.username?.touched) return '';

    if (this.username?.hasError('required'))
      return 'Gebruikersnaam is verplicht.';

    if (this.username?.hasError('email'))
      return 'Gebruikersnaam lijkt niet op een e-mailadres.';

    return '';
  }

  private determinePasswordError(): string {
    if (!this.password?.touched) return '';

    if (this.password?.hasError('required')) return 'Wachtwoord is verplicht.';

    if (this.password?.hasError('email'))
      return 'Wachtwoord lijkt niet op een e-mailadres.';

    return '';
  }
}
