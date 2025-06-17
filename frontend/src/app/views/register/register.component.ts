import {Component, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonModule} from "@angular/material/button";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {AuthShellComponent} from '../../shared/auth-shell/auth-shell.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {RegisterService} from '../../services/register.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [
    MatButtonModule,
    MatCheckboxModule,
    MatInputModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    AuthShellComponent
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  readonly passwordMinLength = 8;
  private readonly registerService = inject(RegisterService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  readonly form = this.formBuilder.group({
    name: ['', [Validators.required]],
    username: ['', [Validators.required, Validators.email]],
    password: ['', [
      Validators.required,
      Validators.minLength(this.passwordMinLength)
    ]],
    terms: ['', [Validators.requiredTrue]],
  });

  async handleSubmit(): Promise<void> {
    if (!this.form.valid) {
      this.form.markAllAsTouched();
      return;
    }

    this.form.disable({onlySelf: false})

    try {
      const values = this.form.getRawValue();
      await this.registerService.register(
        values.name as string,
        values.username as string,
        values.password as string);

      this.snackBar.open(`Account aangemaakt`, undefined, {
        duration: 5000,
        politeness: 'assertive',
      });

      this.router.navigate(['/login']);
    } catch (e) {
      console.error('Register failed = %o', e);
    } finally {
      this.form.enable({onlySelf: false})
    }
  }
}
