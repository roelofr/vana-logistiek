import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserShellComponent } from './user-shell.component';

describe('UserShellComponent', () => {
  let component: UserShellComponent;
  let fixture: ComponentFixture<UserShellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserShellComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserShellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
