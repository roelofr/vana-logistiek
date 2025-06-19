import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectVendor } from './select-vendor';

describe('SelectVendor', () => {
  let component: SelectVendor;
  let fixture: ComponentFixture<SelectVendor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectVendor]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectVendor);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
