package dev.roelofr.rest.request;

import dev.roelofr.Constants;
import dev.roelofr.rest.validation.DistrictExists;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public record UpdateUserRequest(
    Optional<String> name,
    Optional<String> email,
    List<String> roles,
    @DistrictExists Long district
) {
    public @Nullable String cleanEmail() {
        return this.email()
            .map(value -> value.toLowerCase(Constants.LocaleDutch).trim())
            .orElse(null);
    }
}
