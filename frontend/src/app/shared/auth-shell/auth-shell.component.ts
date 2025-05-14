import {Component, computed, input} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {NgTemplateOutlet} from '@angular/common';

@Component({
  selector: 'app-auth-shell',
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    NgTemplateOutlet,
  ],
  templateUrl: './auth-shell.component.html',
  styleUrl: './auth-shell.component.scss'
})
export class AuthShellComponent {
  readonly title = input<string | null>(null);
  readonly subtitle = input<string | null>(null);
  readonly showBack = input(false);
  readonly cardTitle = input.required<string>();

  readonly showTitle = computed(() => Boolean(this.title()));
}
