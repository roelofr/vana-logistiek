package dev.roelofr.rest.validation;

import dev.roelofr.repository.TeamRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class TeamExistsNumericValidator implements ConstraintValidator<TeamExists, Long> {
    private final TeamRepository teamRepository;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        // Does not check explicit nulls
        if (value == null)
            return true;

        if (value <= 0)
            return false;

        return teamRepository.findByIdOptional(value).isPresent();
    }
}
