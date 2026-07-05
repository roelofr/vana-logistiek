package dev.roelofr.domains.chat.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import dev.roelofr.validation.CanSelfValidate;
import dev.roelofr.validation.ValidatedItself;

@ValidatedItself
public record ChatLocationDto(
    @JsonAlias({"lat"})
    double latitude,
    @JsonAlias({"lng", "long"})
    double longitude
) implements CanSelfValidate {
    @Override
    public boolean isValid() {
        // Should loosely match NL
        return latitude >= 50 && latitude <= 53 && longitude >= 3 && longitude <= 7;
    }
}
