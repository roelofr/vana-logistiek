import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AppUiComponent } from './app-ui.component';

describe('AppUiComponent', () => {
  let component: AppUiComponent;
  let fixture: ComponentFixture<AppUiComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [AppUiComponent],
      imports: [
        MatButtonModule,
        MatIconModule,
        MatListModule,
        MatSidenavModule,
        MatToolbarModule,
      ]
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppUiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should compile', () => {
    expect(component).toBeTruthy();
  });
});
