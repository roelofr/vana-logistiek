package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Vendor;
import lombok.Builder;

@Builder
public record VendorHttpDto(long id, String number, String name, DistrictHttpDto district) {
    public VendorHttpDto(Vendor vendor) {
        this(
            vendor.getId(),
            vendor.getNumber(),
            vendor.getName(),
            DistrictHttpDto.fromNullable(vendor.getTeam())
        );
    }
}
