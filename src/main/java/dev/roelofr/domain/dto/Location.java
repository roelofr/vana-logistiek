package dev.roelofr.domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import dev.roelofr.validation.CanSelfValidate;

public record Location(
    @JsonAlias({"lat"})
    double lat,
    @JsonAlias({"lng", "long"})
    double lng
) implements CanSelfValidate {
    @Override
    public boolean isValid() {
        return lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180;
    }
}
