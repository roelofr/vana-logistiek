package dev.roelofr.domains.vendor.dto;

import dev.roelofr.domains.vendor.model.District;

public record DistrictDto(
    long id,
    String name,
    String colour
) {
    public DistrictDto(District district) {
        this(
            district.getId(),
            district.getName(),
            district.getColour()
        );
    }
}
