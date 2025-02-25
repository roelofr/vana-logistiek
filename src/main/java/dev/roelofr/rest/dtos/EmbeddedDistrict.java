package dev.roelofr.rest.dtos;

import dev.roelofr.domain.District;

public record EmbeddedDistrict(
    long id,
    String name,
    String mobileName,
    String colour
) {
    public EmbeddedDistrict(District district) {
        this(
            district.getId(),
            district.getName(),
            district.getMobileName(),
            district.getColour()
        );
    }
}
