package dev.roelofr.rest.dtos;

import dev.roelofr.domain.Vendor;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public record EmbeddedVendor(
    long id,
    String name,
    String number
) {
    public static EmbeddedVendor fromNullable(@Nullable Vendor vendor) {
        if (vendor == null)
            return null;

        return new EmbeddedVendor(vendor);
    }

    public EmbeddedVendor(@Nonnull Vendor vendor) {
        this(
            vendor.getId(),
            vendor.getName(),
            vendor.getNumber()
        );
    }
}
