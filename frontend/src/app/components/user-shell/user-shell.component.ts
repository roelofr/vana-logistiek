import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterOutlet} from '@angular/router';
import {AppShellComponent} from '../../global/app-shell/app-shell.component';

@Component({
  selector: 'app-user-shell',
  imports: [
    AppShellComponent,
    RouterOutlet,
  ],
  templateUrl: './user-shell.component.html'
})
export class UserShellComponent implements OnInit {
  readonly router = inject(Router);
  readonly activatedRoute = inject(ActivatedRoute);

  ngOnInit() {
    console.log('Init called');
    
    if (this.activatedRoute.parent === null)
      this.router.navigate(['/dashboard']);
  }
}
