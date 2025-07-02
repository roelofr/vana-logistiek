import {ComponentFixture, TestBed} from '@angular/core/testing';

import {IndexComponent} from './index.component';
import {provideZonelessChangeDetection} from '@angular/core';

describe('IndexComponent', () => {
  let component: IndexComponent;
  let fixture: ComponentFixture<IndexComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
      imports: [IndexComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(IndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
