package dev.roelofr.rest.validation;

import dev.roelofr.repository.TeamRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class TeamExistsStringValidator implements ConstraintValidator<TeamExists, String> {
    private final TeamRepository teamRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Does not check explicit nulls
        if (value == null)
            return true;

        if (value.isBlank())
            return false;

        return teamRepository.findByName(value).isPresent();
    }
}
