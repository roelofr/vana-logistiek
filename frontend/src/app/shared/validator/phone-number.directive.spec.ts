import {PhoneNumberValidator} from './phone-number.directive';
import {AbstractControl} from '@angular/forms';

describe('ValidPhoneNumberDirective', () => {
  const validate = (value: unknown) => PhoneNumberValidator({value} as AbstractControl);

  it('should validate valid numbers', () => {
    expect(validate('06 11 22 33 44')).toBeNull();
    expect(validate('+31 6 11223344')).toBeNull();
    expect(validate('0031 247 247 247')).toBeNull();
    expect(validate('+33 1 2 5 8 9 6 3')).toBeNull();
  });

  it('should reject invalid numbers', () => {
    expect(validate('+31 06 11 22 33 44')).not.toBeNull();
    expect(validate('0031 06 11 22 33 44')).not.toBeNull();
    expect(validate('+31 6 112233')).not.toBeNull();
    expect(validate('6 88 55 99 66')).not.toBeNull();
  })
});
