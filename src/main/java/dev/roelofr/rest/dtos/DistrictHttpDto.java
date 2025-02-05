package dev.roelofr.rest.dtos;

import dev.roelofr.domain.District;
import lombok.Builder;

@Builder
public record DistrictHttpDto(long id, String name, String colour) {
    public DistrictHttpDto(District district) {
        this(district.id, district.name, district.colour);
    }
}
