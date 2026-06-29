package dev.roelofr.support.validation;

import dev.roelofr.domains.vendor.service.VendorService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class VendorExistsValidator implements ConstraintValidator<VendorExists, Number> {
    private final VendorService vendorService;

    @Override
    @Transactional
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        var longValue = value.longValue();
        if (longValue <= 0)
            return false;

        return vendorService.getVendor(longValue).isPresent();
    }
}
