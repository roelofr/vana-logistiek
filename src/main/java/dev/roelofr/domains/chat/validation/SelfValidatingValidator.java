package dev.roelofr.domains.chat.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SelfValidatingValidator implements ConstraintValidator<ValidatedItself, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // If the value is null, let standard @NotNull handle it,
        // unless you specifically want this to fail on null.
        if (value == null) {
            return true;
        }

        if (!(value instanceof CanSelfValidate selfValidatable)) {
            return false;
        }

        try {
            // Delegate the logic to the object itself
            return selfValidatable.isValid();
        } catch (Exception e) {
            // Log error if necessary, or rethrow
            // In validation context, returning false is safer
            return false;
        }
    }
}
