import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AppUiComponent} from './app-ui.component';
import {provideRouter} from '@angular/router';

describe('AppUiComponent', () => {
  let component: AppUiComponent;
  let fixture: ComponentFixture<AppUiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([{path: '**', component: AppUiComponent}])],
      imports: [AppUiComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AppUiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
