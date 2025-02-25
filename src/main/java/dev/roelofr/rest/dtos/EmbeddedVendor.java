package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Vendor;

public record EmbeddedVendor(
    long id,
    String name,
    String number
) {
    public EmbeddedVendor(Vendor vendor) {
        this(
            vendor.getId(),
            vendor.getName(),
            vendor.getNumber()
        );
    }
}
