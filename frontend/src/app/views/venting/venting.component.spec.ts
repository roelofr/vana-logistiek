import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VentingComponent } from './venting.component';

describe('VentingComponent', () => {
  let component: VentingComponent;
  let fixture: ComponentFixture<VentingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VentingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VentingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
