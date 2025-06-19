import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelectVendor} from './select-vendor';
import {provideZonelessChangeDetection} from '@angular/core';
import {VendorService} from '../../../services/vendor.service';

describe('SelectVendor', () => {
  let component: SelectVendor;
  let fixture: ComponentFixture<SelectVendor>;

  let vendorList = [] as Vendor[];
  let vendorById: null | Vendor = null;
  let vedndorServiceMock = {
    getAll() {
      return Promise.resolve(vendorList);
    },
    getByIdCached(id: number) {
      return Promise.resolve(vendorById);
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        {provide: VendorService, useValue: vedndorServiceMock},
      ],
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
