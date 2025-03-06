package dev.roelofr.rest.dtos;

import dev.roelofr.domain.District;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record DistrictHttpDto(long id, String name, String mobileName, String colour) {
    public static DistrictHttpDto fromNullable(@Nullable District district) {
        if (district == null)
            return null;

        return new DistrictHttpDto(district);
    }

    public DistrictHttpDto(@Nonnull District district) {
        this(district.getId(), district.getName(), district.getMobileName(), district.getColour());
    }
}
