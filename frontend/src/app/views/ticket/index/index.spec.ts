import {ComponentFixture, TestBed} from '@angular/core/testing';

import {Index} from './index';
import {provideZonelessChangeDetection} from '@angular/core';

describe('Index', () => {
  let component: Index;
  let fixture: ComponentFixture<Index>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideZonelessChangeDetection()],
      imports: [Index]
    })
      .compileComponents();

    fixture = TestBed.createComponent(Index);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
