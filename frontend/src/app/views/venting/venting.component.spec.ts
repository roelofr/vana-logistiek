import {ComponentFixture, TestBed} from '@angular/core/testing';

import {VentingComponent} from './venting.component';
import {provideZonelessChangeDetection} from '@angular/core';

describe('VentingComponent', () => {
  let component: VentingComponent;
  let fixture: ComponentFixture<VentingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
      ],
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
