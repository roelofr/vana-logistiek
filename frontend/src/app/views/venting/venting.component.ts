import {Component} from '@angular/core';
import {MatCardModule} from '@angular/material/card';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {VentingDialogComponent} from './venting-dialog/venting-dialog.component';
import {AppContainerComponent} from '../../global/app-container/app-container.component';

@Component({
  selector: 'app-venting',
  imports: [
    AppContainerComponent,
    MatCardModule,
    MatButtonModule,
    MatDialogModule,
  ],
  templateUrl: './venting.component.html',
  styleUrl: './venting.component.scss'
})
export class VentingComponent {
  constructor(private readonly dialog: MatDialog) {
    //
  }

  handleClickStart() {
    this.dialog.open(VentingDialogComponent);
  }
}
