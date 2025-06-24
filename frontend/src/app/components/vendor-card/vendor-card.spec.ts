import {ComponentFixture, TestBed} from '@angular/core/testing';

import {VendorCard} from './vendor-card';
import {provideZonelessChangeDetection} from '@angular/core';
import {Vendor} from '../../app.domain';

describe('VendorCard', () => {
  let component: VendorCard;
  let fixture: ComponentFixture<VendorCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
      imports: [VendorCard]
    })
      .compileComponents();

    fixture = TestBed.createComponent(VendorCard);

    fixture.componentRef.setInput('vendor', {
      id: 8,
      name: 'Test Vendor',
      number: '400a',
    } as Partial<Vendor>);

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
