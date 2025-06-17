package dev.roelofr.rest.validation;

import dev.roelofr.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UserExistsValidator implements ConstraintValidator<UserExists, Long> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        // Does not check explicit nulls
        if (value == null)
            return true;

        if (value <= 0)
            return false;

        return userRepository.findByIdOptional(value).isPresent();
    }
}
