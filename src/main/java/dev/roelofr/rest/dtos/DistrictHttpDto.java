package dev.roelofr.rest.dtos;

import dev.roelofr.domain.District;
import lombok.Builder;

@Builder
public record DistrictHttpDto(long id, String name, String mobileName, String colour) {
    public DistrictHttpDto(District district) {
        this(district.getId(), district.getName(), district.getMobileName(), district.getColour());
    }
}
