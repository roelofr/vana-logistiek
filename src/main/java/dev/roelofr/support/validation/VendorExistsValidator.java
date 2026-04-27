package dev.roelofr.support.validation;

import dev.roelofr.domains.vendor.service.VendorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class VendorExistsValidator implements ConstraintValidator<VendorExists, Number> {
    private final VendorService vendorService;

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        var longValue = value.longValue();
        if (longValue <= 0)
            return false;

        return vendorService.getVendor(longValue).isPresent();
    }
}
