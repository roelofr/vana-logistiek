package dev.roelofr.rest.dtos;

import lombok.Builder;

@Builder
public record VendorHttpDto(long id, String number, String name, DistrictHttpDto district) {
    public VendorHttpDto(Vendor vendor) {
        this(
            vendor.getId(),
            vendor.getNumber(),
            vendor.getName(),
            DistrictHttpDto.fromNullable(vendor.getDistrict())
        );
    }
}
