package dev.roelofr.rest.validation;

import dev.roelofr.repository.DistrictRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class DistrictExistsStringValidator implements ConstraintValidator<DistrictExists, String> {
    private final DistrictRepository districtRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Does not check explicit nulls
        if (value == null)
            return true;

        if (value.isBlank())
            return false;

        return districtRepository.find("slug", value.toLowerCase()).singleResultOptional().isPresent();
    }
}
