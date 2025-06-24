import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelectType} from './select-type';
import {provideZonelessChangeDetection} from '@angular/core';

describe('SelectType', () => {
  let component: SelectType;
  let fixture: ComponentFixture<SelectType>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
      imports: [SelectType]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SelectType);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
