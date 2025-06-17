package dev.roelofr.rest.dtos;

import dev.roelofr.domain.District;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record EmbeddedDistrict(
    long id,
    String name,
    String mobileName,
    String colour
) {
    public EmbeddedDistrict(@Nonnull District district) {
        this(
            district.getId(),
            district.getName(),
            district.getMobileName(),
            district.getColour()
        );
    }

    public static EmbeddedDistrict fromNullable(@Nullable District district) {
        if (district == null)
            return null;

        return new EmbeddedDistrict(district);
    }
}
