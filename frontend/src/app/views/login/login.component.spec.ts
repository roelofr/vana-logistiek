import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginComponent} from './login.component';
import {provideExperimentalZonelessChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {TestbedHarnessEnvironment} from '@angular/cdk/testing/testbed';
import {MatInputHarness} from '@angular/material/input/testing';
import {HarnessLoader} from '@angular/cdk/testing';

describe('LoginComponent', () => {
  let loader: HarnessLoader;
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideHttpClient(withFetch()),
        provideHttpClientTesting(),
        provideRouter([{path: '**', component: LoginComponent}]),
      ],
      imports: [LoginComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load all input harnesses', async () => {
    const inputs = await loader.getAllHarnesses(MatInputHarness);
    expect(inputs.length).toBe(2);
  });

  it('should load input with a specific name', async () => {
    const inputs = await loader.getAllHarnesses(MatInputHarness.with({selector: '[formControlName=username]'}));
    expect(inputs.length).toBe(1);
  });

  it('should be able to set value of input', async () => {
    const inputs = await loader.getAllHarnesses(MatInputHarness);
    const input = inputs[0];
    await input.setValue('Steve');

    expect(await input.getValue()).toBe('Steve');
  });

  it('should be able to get disabled state', async () => {
    const inputs = await loader.getAllHarnesses(MatInputHarness);
    expect(inputs.length).toBe(2);

    expect(await inputs[0].isDisabled()).toBe(false);
    expect(await inputs[1].isDisabled()).toBe(false);
  });

  it('should be able to get type of input', async () => {
    const inputs = await loader.getAllHarnesses(MatInputHarness);
    expect(inputs.length).toBe(3);

    expect(await inputs[0].getType()).toBe('email');
    expect(await inputs[1].getType()).toBe('password');
  });
});
