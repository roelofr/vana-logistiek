import {AbstractControl, ValidationErrors} from '@angular/forms';

const nonNumberParts = /^[^0-9+]|(?<!^)[^0-9]+/g;
const validPhoneNumber = /^(\+31|0031|0)[1-9][0-9]{8}|(\+|00)(?!31)[1-9][0-9]{8,12}$/;

/**
 * @description
 * Validator that requires the control contains a valid Dutch phone number.
 *
 * @usageNotes
 *
 * ### Validate that the field has a valid phone number
 *
 * ```ts
 * const control = new FormControl('088', PhoneNumberValidator);
 *
 * console.log(control.errors); // {phonenumber: true}
 * ```
 *
 * @returns An error map with the `phonenumber` property
 * if the validation check fails, otherwise `null`.
 *
 */
export function PhoneNumberValidator(control: AbstractControl): ValidationErrors | null {
  if (!control.value)
    return null;

  const cleanNumber = String(control.value).replaceAll(nonNumberParts, '');
  if (validPhoneNumber.test(cleanNumber))
    return null;

  return {phonenumber: true};
}
