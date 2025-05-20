import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VentingDialogComponent } from './venting-dialog.component';

describe('VentingDialogComponent', () => {
  let component: VentingDialogComponent;
  let fixture: ComponentFixture<VentingDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VentingDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VentingDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
